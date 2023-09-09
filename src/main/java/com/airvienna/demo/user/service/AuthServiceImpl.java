package com.airvienna.demo.user.service;

import com.airvienna.demo.exception.InvalidCredentialsException;
import com.airvienna.demo.exception.InvalidRefreshTokenException;
import com.airvienna.demo.security.jwt.JwtTokenProvider;
import com.airvienna.demo.user.Mapper.UserMapper;
import com.airvienna.demo.user.domain.User;
import com.airvienna.demo.user.dto.RequestLoginDto;
import com.airvienna.demo.security.jwt.dto.TokenDto;
import com.airvienna.demo.user.dto.RequestRegenerateToken;
import com.airvienna.demo.user.dto.RequestUserDto;
import com.airvienna.demo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate redisTemplate;

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long signUp(RequestUserDto requestUserDto) {
        User user = userMapper.requestToEntity(requestUserDto);
        return userRepository.save(user).getId();
    }

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
        String redisRefreshToken = (String) redisTemplate.opsForValue().get(authentication.getName());
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