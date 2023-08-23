package com.airvienna.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 스프링 빈으로 등록
public class WebMvcConfig implements WebMvcConfigurer {
    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 요청 경로(/**)에 대한 CORS 설정을 지정
        registry.addMapping("/**")
                // 허용할 원본 도메인 지정
                .allowedOrigins("http://localhost:3000")
                // 허용할 HTTP 메서드 지정
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // 요청 헤더 중 어떤 것들을 허용할 것인지 지정
                .allowedHeaders("*")
                // 쿠키나 HTTP 인증 등이 필요한 경우 해당 설정을 허용
                .allowCredentials(true)
                // 브라우저가 해당 CORS 설정을 캐싱할 시간(초 단위) 지정
                .maxAge(MAX_AGE_SECS);
    }
}