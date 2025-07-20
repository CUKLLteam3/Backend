package com.example.cukllteam3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// 모든 API 경로 허용
                .allowedOrigins(

                        "http://localhost:5173",
                        "http://localhost:5174"
                )
                .allowedMethods("*");// GET, POST, PUT, DELETE 등 모든 HTTP 메서드 허용
        //.allowCredentials(true); // 인증 정보(쿠키 등) 포함 허용
    }
}
