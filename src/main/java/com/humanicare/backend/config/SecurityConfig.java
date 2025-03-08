package com.humanicare.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanicare.backend.jwt.filter.JwtAuthenticationProcessingFilter;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.logout.CustomLogoutFilter;
import com.humanicare.backend.repository.UserRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리 JwtAuthenticationProcessingFilter는
 * AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(
                        corsConfigurationSource())) // cors 해제
                .formLogin(config -> config.disable()) // FormLogin 사용 X
                .logout(config -> config.disable())    // logout 사용 X
                .httpBasic(config -> config.disable()) // httpBasic 사용 X
                .csrf(config -> config.disable()) // csrf 보안 사용 X
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()
                                .requestMatchers("/api/spring/reissue").permitAll() // refreshToken 재발급 가능
                                .requestMatchers("/api/spring/oauth/**").permitAll() // OAuth 경로 접근 가능
                                .requestMatchers("/api/spring/google-login/**").permitAll() // 구글 로그인
                                .requestMatchers("/health").permitAll() // aws health check
                                .anyRequest().authenticated() // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능
                );
        // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : CustomLogout -> LogoutFilter -> JwtAuthenticationProcessingFilter
        http.addFilterBefore(customLogoutFilter(), LogoutFilter.class);
        http.addFilterAfter(jwtAuthenticationProcessingFilter(), CustomLogoutFilter.class);

        return http.build();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000",
                        "http://localhost:8000",
                        "http://react:3000",
                        "http://fastapi:8000")); // 허용할 도메인 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 설정
        configuration.setAllowedHeaders(Arrays.asList("*")); // 허용할 헤더 설정
        configuration.setAllowCredentials(true); // 자격 증명 허용 여부 설정
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService,
                userRepository);
        return jwtAuthenticationFilter;
    }

    @Bean
    public CustomLogoutFilter customLogoutFilter() {
        CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(jwtService);
        return customLogoutFilter;
    }
}
