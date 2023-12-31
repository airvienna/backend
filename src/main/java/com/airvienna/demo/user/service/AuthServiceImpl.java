package com.airvienna.demo.user.service;

import com.airvienna.demo.security.execption.InvalidTokenException;
import com.airvienna.demo.user.exception.DuplicateEmailException;
import com.airvienna.demo.user.exception.DuplicatePhoneException;
import com.airvienna.demo.user.exception.InvalidCredentialsException;
import com.airvienna.demo.user.exception.InvalidRefreshTokenException;
import com.airvienna.demo.security.jwt.JwtTokenProvider;
import com.airvienna.demo.user.Mapper.UserMapper;
import com.airvienna.demo.user.domain.User;
import com.airvienna.demo.user.dto.RequestLoginDto;
import com.airvienna.demo.security.jwt.dto.TokenDto;
import com.airvienna.demo.user.dto.RequestRegenerateToken;
import com.airvienna.demo.user.dto.RequestUserDto;
import com.airvienna.demo.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.access-token.expiration-time}")
    private long accessExpirationTime;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate redisTemplate;

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /**
     * 회원가입
     */
    @Override
    @Transactional
    public TokenDto signUp(RequestUserDto requestUserDto) {
        // 유저의 정보 저장
        User user = userMapper.requestToEntity(requestUserDto);

        // 이미 가입한 Email이면 409 CONFLCT
        if (userRepository.existsByEmail(requestUserDto.getEmail())) {
            throw new DuplicateEmailException("An account with this email already exists.");
        }
        // 이미 가입한 전화번호이면 409 CONFLCT
        else if (requestUserDto.getPhone() != null && userRepository.existsByPhone(requestUserDto.getPhone())) {
            throw new DuplicatePhoneException("An account with this phone number already exists.");
        }

        userRepository.save(user).getId();

        // JWT 토큰 반환
        TokenDto tokenDto = login(new RequestLoginDto(requestUserDto.getEmail(), requestUserDto.getPassword()));
        return tokenDto;
    }

    /**
     * 로그인
     */
    @Override
    @Transactional
    public TokenDto login(RequestLoginDto requestLoginDto) {
        try {
            // 사용자의 이메일과 비밀번호를 사용하여 인증을 시도
            // 인증에 실패하면 `BadCredentialsException`을 발생
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLoginDto.getEmail(),
                            requestLoginDto.getPassword()
                    )
            );

            // JWT token 발급
            TokenDto tokenDto = new TokenDto(
                    jwtTokenProvider.createAccessToken(authentication),
                    jwtTokenProvider.createRefreshToken(authentication)
            );

            return tokenDto;

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("The provided email or password is incorrect.");
        }
    }

    /**
     * 로그아웃
     */
    @Override
    @Transactional
    public void logout(TokenDto tokenDto) {
        Authentication authentication = null;
        boolean accessTokenValid = jwtTokenProvider.validateToken(tokenDto.getAccessToken());
        boolean refreshTokenValid = jwtTokenProvider.validateToken(tokenDto.getRefreshToken());

        if (!accessTokenValid && !refreshTokenValid) {
            return;
        }

        if (accessTokenValid) {
            // Access Token에서 Authentication 정보 획득
            authentication = jwtTokenProvider.getAuthentication(tokenDto.getAccessToken());

            Date tokenExpirationTime = jwtTokenProvider.getExpirationDateFromToken(tokenDto.getAccessToken());
            Date now = new Date();
            long remainingTime = tokenExpirationTime.getTime() - now.getTime();

            // Access token을 블랙리스트에 등록
            redisTemplate.opsForValue().set(
                    "accessTokenBlackList:" + authentication.getName(),
                    tokenDto.getAccessToken(),
                    remainingTime,
                    TimeUnit.MILLISECONDS
            );
        }

        if (refreshTokenValid) {
            // authentication이 null 이면
            // Refresh Token에서 Authentication 정보 획득
            if(authentication == null) {
                authentication = jwtTokenProvider.getAuthentication(tokenDto.getRefreshToken());
            }

            // Refresh token을 삭제
            redisTemplate.delete("refreshToken:" + authentication.getName());
        }
    }


    /**
     * 토큰 갱신
     */
    @Override
    @Transactional
    public TokenDto regenerateToken(RequestRegenerateToken requestRegenerateToken) {
        String refreshToken = requestRegenerateToken.getRefreshToken();

        // JWT를 통한 Refresh Token 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("The refresh token is invalid.");
        }

        // Refresh Token에서 Authentication 정보 획득
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        // Redis에서 Refresh Token 값 확인
        String redisRefreshToken = (String) redisTemplate.opsForValue().get("refreshToken:" + authentication.getName());
        if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
            throw new InvalidRefreshTokenException("The refresh token is invalid.");
        }

        // 새로운 Access Token 및 Refresh Token 발급
        TokenDto tokenDto = new TokenDto(
                jwtTokenProvider.createAccessToken(authentication),
                jwtTokenProvider.createRefreshToken(authentication)
        );

        return tokenDto;
    }

}