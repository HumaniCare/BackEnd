package com.humanicare.backend.logout;

import com.humanicare.backend.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private static final String LOGOUT_URI = "/logout";
    private static final String LOGOUT_METHOD = "POST";

    private final JwtService jwtService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        if (!isLogoutRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        Optional<String> refreshToken = jwtService.extractRefreshToken(request);
        if (refreshToken.isEmpty() || !isTokenValid(refreshToken.get())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        deleteRefreshTokenInRedis(response, refreshToken.get());
    }

    private boolean isLogoutRequest(final HttpServletRequest request) {
        return request.getRequestURI().equals(LOGOUT_URI) && request.getMethod().equals(LOGOUT_METHOD);
    }

    private boolean isTokenValid(final String refreshToken) {
        try {
            jwtService.isTokenValid(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void deleteRefreshTokenInRedis(final HttpServletResponse response, final String refreshToken) {
        jwtService.deleteRefreshToken(refreshToken);

        Cookie cookie = new Cookie(jwtService.getRefreshHeader(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
