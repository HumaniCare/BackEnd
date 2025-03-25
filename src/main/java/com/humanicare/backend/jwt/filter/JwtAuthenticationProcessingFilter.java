// JwtAuthenticationProcessingFilter.java
package com.humanicare.backend.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.exception.TokenInvalidException;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.jwt.util.PasswordUtil;
import com.humanicare.backend.oauth.OauthServerType;
import com.humanicare.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final Set<String> SWAGGER_PATH_PREFIXES = Set.of(
            "/swagger", "/v3/api-docs", "/api-docs", "/swagger-ui.html", "/swagger-ui",
            "/webjars", "/favicon.ico", "/csrf", "/v3/api-docs.yaml", "/v3/api-docs.json",
            "/swagger-resources", "/swagger-resources/configuration/ui", "/swagger-resources/configuration/security"
    );

    private static final Set<String> NOT_APPLY_JWT_FILTER_PREFIXES = Set.of(
            "/api/spring/reissue", "/api/spring/oauth", "/api/spring/google-login", "/health"
    );

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        if (isSwaggerPath(request) || isNotApplyJwtPath(request)) {
            log.debug("JWT Authentication Filter Skip");
            filterChain.doFilter(request, response);
            return;
        }
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    public void checkAccessTokenAndAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                                  final FilterChain filterChain) throws IOException, ServletException {
        if (isSwaggerPath(request)) {
            log.info("Swagger 토큰 미필요");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        if (accessToken == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다");
            return;
        }

        try {
            jwtService.isTokenValid(accessToken);
            var oauthIdOpt = jwtService.extractOauthId(accessToken);
            var providerOpt = jwtService.extractOauthServerType(accessToken);

            if (oauthIdOpt.isPresent() && providerOpt.isPresent()) {
                userRepository.findByOauthId_OauthServerIdAndOauthId_OauthServerType(
                        oauthIdOpt.get(), providerOpt.get()
                ).ifPresent(this::saveAuthentication);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰 정보가 부족합니다.");
                return;
            }
        } catch (TokenInvalidException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (RuntimeException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 오류 : " + e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(final User user) {
        UserDetails userDetailsUser = getUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isNotApplyJwtPath(final HttpServletRequest request) {
        String uri = request.getRequestURI();
        return NOT_APPLY_JWT_FILTER_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    private boolean isSwaggerPath(final HttpServletRequest request) {
        String uri = request.getRequestURI();
        return SWAGGER_PATH_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    private UserDetails getUserDetails(final User user) {
        String password = PasswordUtil.generateRandomPassword();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .roles(user.getRole().name())
                .build();
    }

    private void sendErrorResponse(final HttpServletResponse response, final int statusCode, final String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse(statusCode, message);
        mapper.writeValue(out, errorResponse);
    }

    public static class ErrorResponse {
        @Getter
        @Setter
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
