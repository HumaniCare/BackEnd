package com.humanicare.backend.oauth.google;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
public record GoogleOauthConfig(
        String clientId,
        String clientSecret,
        String redirectUri,
        String[] scope
) {
}
