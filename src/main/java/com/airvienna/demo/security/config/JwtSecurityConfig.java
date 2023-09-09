package com.airvienna.demo.security.config;

import com.airvienna.demo.security.jwt.JwtTokenProvider;
import com.airvienna.demo.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JwtTokenProvider과 JwtFilter를 SecurityConfig에 적용
 */
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtFilter customFilter = new JwtFilter(jwtTokenProvider, redisTemplate);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
