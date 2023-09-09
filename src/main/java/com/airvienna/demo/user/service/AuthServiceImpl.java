package com.airvienna.demo.user.service;

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
    public TokenDto signUp(RequestUserDto requestUserDto) {
        // 유저의 정보 저장
        User user = userMapper.requestToEntity(requestUserDto);
        userRepository.save(user).getId();

        // JWT 토큰 반환
        TokenDto tokenDto = login(new RequestLoginDto(requestUserDto.getEmail(), requestUserDto.getPassword()));
        return tokenDto;
    }

    @Override
    @Transactional
    public TokenDto login(RequestLoginDto requestLoginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLoginDto.getEmail(),
                            requestLoginDto.getPassword()
                    )
            );

            TokenDto tokenDto = new TokenDto(
                    jwtTokenProvider.createAccessToken(authentication),
                    jwtTokenProvider.createRefreshToken(authentication)
            );

            return tokenDto;

        }catch(BadCredentialsException e){
            log.error("잘못된 사용자 비밀번호입니다."); // INVALID_USER_PW.getMessage()를 대체한 에러 메시지입니다.
            throw new RuntimeException("잘못된 사용자 비밀번호입니다."); // BaseException을 RuntimeException으로 변경했습니다.
        }
    }

    @Override
    @Transactional
    public TokenDto regenerateToken(RequestRegenerateToken requestRegenerateToken) {
        String refreshToken = requestRegenerateToken.getRefreshToken();

        // 1. JWT를 통한 Refresh Token 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.error("잘못된 refresh token입니다.");
            throw new RuntimeException("잘못된 refresh token입니다.");
        }

        // 2. Refresh Token에서 Authentication 정보 획득
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        // 3. Redis에서 Refresh Token 값 확인
        String redisRefreshToken = (String)redisTemplate.opsForValue().get(authentication.getName());
        if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
            log.error("Redis에 저장된 refresh token이 일치하지 않습니다.");
            throw new RuntimeException("Redis에 저장된 refresh token이 일치하지 않습니다.");
        }

        // 4. 새로운 Access Token 및 Refresh Token 발급
        TokenDto tokenDto = new TokenDto(
                jwtTokenProvider.createAccessToken(authentication),
                jwtTokenProvider.createRefreshToken(authentication)
        );

        return tokenDto;
    }

}