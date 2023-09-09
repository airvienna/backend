package com.airvienna.demo.user.controller;

import com.airvienna.demo.security.jwt.dto.TokenDto;
import com.airvienna.demo.user.dto.RequestLoginDto;
import com.airvienna.demo.user.dto.RequestRegenerateToken;
import com.airvienna.demo.user.dto.RequestUserDto;
import com.airvienna.demo.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/api/auth"))
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signUp(@Valid @RequestBody RequestUserDto requestUserDto) {
        TokenDto tokenDto = authService.signUp(requestUserDto);

        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody RequestLoginDto requestLoginDto) {
        TokenDto tokenDto = authService.login(requestLoginDto);
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/regenerationToken")
    public ResponseEntity<TokenDto> regenerationToken(@RequestBody RequestRegenerateToken requestRegenerateToken) {
        TokenDto tokenDto = authService.regenerateToken(requestRegenerateToken);
        return ResponseEntity.ok(tokenDto);
    }
}