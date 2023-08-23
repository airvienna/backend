package com.airvienna.demo.user.controller;

import com.airvienna.demo.user.dto.RequestUserDto;
import com.airvienna.demo.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api"))
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/users")
    public ResponseEntity<Long> signUp(@Valid @RequestBody RequestUserDto request) {
        Long userId = authService.signUp(request);
        return ResponseEntity.ok(userId);
    }
}