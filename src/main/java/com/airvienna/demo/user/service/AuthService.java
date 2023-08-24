package com.airvienna.demo.user.service;

import com.airvienna.demo.security.jwt.dto.TokenDto;
import com.airvienna.demo.user.dto.RequestLoginDto;
import com.airvienna.demo.user.dto.RequestRegenerateToken;
import com.airvienna.demo.user.dto.RequestUserDto;

public interface AuthService {
    // 회원가입
    Long signUp(RequestUserDto requestUserDto);

    // 로그인
    TokenDto login(RequestLoginDto requestLoginDto);

    // 토큰 재발급
    TokenDto regenerateToken(RequestRegenerateToken requestRegenerateToken);
}
