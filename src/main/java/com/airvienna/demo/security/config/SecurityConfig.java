package com.airvienna.demo.security.config;

import com.airvienna.demo.security.jwt.JwtAccessDeniedHandler;
import com.airvienna.demo.security.jwt.JwtAuthenticationEntryPoint;
import com.airvienna.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final HandlerMappingIntrospector introspector;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF(크로스 사이트 요청 위조) 보안을 비활성화합니다. 주로 REST API에서 사용됩니다.
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 토큰으로 인증하기 때문에 세션을 생성하지 않습니다.
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/auth/**")).permitAll() // 이 주소는 인증 없이 접근 가능합니다.
                        .anyRequest().authenticated())// 나머지 주소는 인증이 필요합니다.
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler) // 접근 거부시 사용할 핸들러입니다.
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패시 시작되는 포인트입니다.
                );

        // JWT 보안 설정을 적용합니다.
        http.apply(new JwtSecurityConfig(jwtTokenProvider));
        return http.build();
    }
}