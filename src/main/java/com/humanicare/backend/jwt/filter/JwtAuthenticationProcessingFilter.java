package com.humanicare.backend.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.exception.TokenInvalidException;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.jwt.util.PasswordUtil;
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


/**
 * Jwt 인증 필터 로그인 이외의 URI 요청이 왔을 때 처리하는 필터
 * <p>
 * AccessToken 유효성 검사를 진행하고 유효하지 않으면 프론트로 에러 메시지를 보낸다. 프론트에서 에러 메시지를 받으면 재발급 API CALL
 */

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

    /**
     * [AccessToken 체크 & 인증 처리 메소드] request에서 extractAccessToken()으로 AccessToken 추출 후, isTokenValid()로 유효한 토큰인지 검증 유효한
     * 토큰이면, AccessToken서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환 그 유저 객체를
     * saveAuthentication()으로 인증 처리하여 인증 허가 처리된 객체를 SecurityContextHolder에 담기 그 후 다음 인증 필터로 진행
     */
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
            jwtService.extractEmail(accessToken)
                    .flatMap(userRepository::findByEmail)
                    .ifPresent(this::saveAuthentication);
        } catch (TokenInvalidException e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (RuntimeException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 오류 : " + e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * [인증 허가 메소드] 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     * <p>
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성 UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보) 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거) 3. Collection < ? extends
     * GrantedAuthority>로, UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에, new
     * NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     * <p>
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후, setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한
     * 인증 허가 처리
     */
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

    // 오류 정보 {status, message}를 JSON 형태로 바꿔서 응답
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
