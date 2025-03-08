package com.humanicare.backend.jwt.service;


import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.humanicare.backend.apiPayload.code.status.ErrorStatus;
import com.humanicare.backend.apiPayload.exception.handler.AccessTokenHandler;
import com.humanicare.backend.apiPayload.exception.handler.RefreshTokenHandler;
import com.humanicare.backend.domain.RefreshToken;
import com.humanicare.backend.exception.TokenInvalidException;
import com.humanicare.backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Getter
@Slf4j
public class JwtService {

    private static final String SUCCESS_TOKEN = "AccessToken, RefreshToken 발급 완료";
    private static final String INVALID_ACCESS_TOKEN = "AccessToken이 유효하지 않습니다.";
    private static final String INVALID_TOKEN = "유효하지 않은 토큰";
    private static final String RUNTIME_ERROR_TOKEN_CHECK = "토큰 검증 중 런타임 오류 발생";

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Integer accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Integer refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    // 테스트용 토큰 생성 메서드 추가
    public String generateTestToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim("unique_id", UUID.randomUUID().toString())
                .withClaim(EMAIL_CLAIM, "test@example.com")
                .sign(Algorithm.HMAC512(secretKey));
    }

    // 테스트용 토큰 생성 메서드 추가
    public String generateTest2Token() {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim("unique_id", UUID.randomUUID().toString())
                .withClaim(EMAIL_CLAIM, "test2@example.com")
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * AccessToken 생성 메서드
     */
    public String createAccessToken(final String email) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim("unique_id", UUID.randomUUID().toString())
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .withClaim("unique_id", UUID.randomUUID().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * AccessToken을 response Header에 담기 : 발급
     */
    public void sendAccessToken(final HttpServletResponse response, final String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(accessHeader, accessToken);
    }

    /**
     * RefreshToken을 Cookie에 담기
     */
    public void sendRefreshToken(final HttpServletResponse response, final String refreshToken) {
        response.addCookie(createCookie(refreshHeader, refreshToken));
    }

    /**
     * AccessToken을 헤더에, RefreshToken을 쿠키에 담아서 보내기 - 토큰 발급
     */
    public void sendAccessAndRefreshToken(final HttpServletResponse response, final String accessToken,
                                          final String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        sendAccessToken(response, accessToken);
        sendRefreshToken(response, refreshToken);
        log.info(SUCCESS_TOKEN);
    }

    /**
     * Cookie에서 RefreshToken 추출하기
     */
    public Optional<String> extractRefreshToken(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(refreshHeader))
                .map(Cookie::getValue)
                .findFirst()
                .filter(refresh -> !refresh.isEmpty())
                .map(Optional::of)
                .orElseThrow(() -> new RefreshTokenHandler(ErrorStatus._REFRESHTOKEN_NOT_FOUND));
    }

    /**
     * Request Header에서 AccessToken 추출하기
     */
    public Optional<String> extractAccessToken(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ACCESS_TOKEN_REPLACEMENT));
    }

    /**
     * AccessToken에서 email을 추출하기
     */
    public Optional<String> extractEmail(final String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error(INVALID_ACCESS_TOKEN);
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        }
    }

    /**
     * Redis에서 RefreshToken 삭제
     */
    public void deleteRefreshToken(final String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    /**
     * Redis에 RefreshToken 저장
     */
    public void updateRefreshToken(final String email, final String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .email(email)
                .refresh(refreshToken)
                .expiration(refreshTokenExpirationPeriod)
                .build();
        redisTemplate.opsForValue()
                .set(refreshToken, token, (long) refreshTokenExpirationPeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * RefreshToken이 blacklist인지 확인한다
     */
    public boolean isBlackList(final String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken) == null;
    }

    /**
     * RefreshToken에서 email 가져오기
     */
    public String getEmailFromRefreshToken(final String refreshToken) {
        return Optional.ofNullable((RefreshToken) redisTemplate.opsForValue().get(refreshToken))
                .map(RefreshToken::getEmail)
                .orElse(null);
    }

    /**
     * 토큰 유효성 검사
     */
    public void isTokenValid(final String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalidException(INVALID_TOKEN);
        } catch (Exception e) {
            throw new RuntimeException(RUNTIME_ERROR_TOKEN_CHECK, e);
        }
    }

    /**
     * 쿠키 생성
     */
    private Cookie createCookie(final String key, final String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(refreshTokenExpirationPeriod);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }

}
