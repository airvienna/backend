package com.airvienna.demo.security.config;

import com.airvienna.demo.security.jwt.JwtAccessDeniedHandler;
import com.airvienna.demo.security.jwt.JwtAuthenticationEntryPoint;
import com.airvienna.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
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
    private final RedisTemplate redisTemplate;
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
        // CSRF(크로스 사이트 요청 위조) 보안을 비활성화합니다. 주로 REST API에서 사용됩니다.
        http.csrf(AbstractHttpConfigurer::disable);

        // JWT 토큰으로 인증하기 때문에 세션을 생성하지 않습니다.
        http.sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorize) -> authorize
                // H2 Console에 대한 접근 허용
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                // 이 주소는 인증 없이 접근 가능
                .requestMatchers(new MvcRequestMatcher(introspector, "/api/auth/**")).permitAll()
                // 나머지 주소는 인증이 필요
                .anyRequest().authenticated());

        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                // 접근 거부시 사용할 핸들러
                .accessDeniedHandler(jwtAccessDeniedHandler)
                // 인증 실패시 시작되는 포인트
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );

        // h2-console 사용을 위해 SAME ORIGIN에 대하여 iframe 허용
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));

        // JWT 보안 설정을 적용
        http.apply(new JwtSecurityConfig(jwtTokenProvider, redisTemplate));
        return http.build();
    }
}