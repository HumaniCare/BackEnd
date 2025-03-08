package com.humanicare.backend.oauth.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public record KakaoOauthConfig(
        String clientId,
        String clientSecret,
        String redirectUri,
        String[] scope
) {
}
