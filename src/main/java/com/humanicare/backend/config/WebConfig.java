package com.humanicare.backend.config;

import com.humanicare.backend.oauth.OauthServerTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OauthServerTypeConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 로컬 도메인
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080");
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080");
    }
}
