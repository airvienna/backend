package com.airvienna.demo.user.service;

import com.airvienna.demo.security.jwt.dto.TokenDto;
import com.airvienna.demo.user.dto.RequestLoginDto;
import com.airvienna.demo.user.dto.RequestRegenerateToken;
import com.airvienna.demo.user.dto.RequestUserDto;

public interface AuthService {
    /**
     * 회원가입
     */
    TokenDto signUp(RequestUserDto requestUserDto);

    /**
     * 로그인
     */
    TokenDto login(RequestLoginDto requestLoginDto);

    /**
     * 로그아웃
     */
    void logout(TokenDto tokenDto);

    /**
     * 토큰 갱신
     */
    TokenDto regenerateToken(RequestRegenerateToken requestRegenerateToken);
}
