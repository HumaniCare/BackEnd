package com.humanicare.backend.jwt.service;

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
import com.humanicare.backend.oauth.OauthServerType;
import com.humanicare.backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String OAUTH_ID_CLAIM = "oauth_id";
    private static final String PROVIDER_CLAIM = "provider";
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

    /**
     * Lombok의 RequiredArgsConstructor의 경우 @Qualifier가 생성자 parameter로 복사되지 않을 수 있음.
     * NoUniqueBeanDefinitionException 발생 가능.
     * @param redisTemplate
     * @param userRepository
     */
    public JwtService(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
                      UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }



    /**
     * Swagget 용 Test Token생성
     */
    public String generateTestToken() {
        return createAccessToken("test-oauth-id-1", OauthServerType.KAKAO);
    }

    public String generateTest2Token() {
        return createAccessToken("test-oauth-id-2", OauthServerType.GOOGLE);
    }

    /**
     * Oauth 기반 AccessToken 생성
     */
    public String createAccessToken(final String oauthId, final OauthServerType provider) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(OAUTH_ID_CLAIM, oauthId)
                .withClaim(PROVIDER_CLAIM, provider.name())
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
     * AccessToken을 응답 헤더에 추가
     */
    public void sendAccessToken(final HttpServletResponse response, final String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(accessHeader, accessToken);
    }

    /**
     * RefreshToken을 쿠키에 추가
     */
    public void sendRefreshToken(final HttpServletResponse response, final String refreshToken) {
        response.addCookie(createCookie(refreshHeader, refreshToken));
    }

    /**
     * AccessToken + RefreshToken 전송
     */
    public void sendAccessAndRefreshToken(final HttpServletResponse response, final String accessToken,
                                          final String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        sendAccessToken(response, accessToken);
        sendRefreshToken(response, refreshToken);
    }

    /**
     * 쿠키에서 RefreshToken 추출
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
     * 요청 헤더에서 AccessToken 추출
     */
    public Optional<String> extractAccessToken(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.substring(BEARER.length()));
    }

    /**
     * AccessToken에서 oauth_id 추출
     */
    public Optional<String> extractOauthId(final String token) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token)
                    .getClaim(OAUTH_ID_CLAIM)
                    .asString());
        } catch (Exception e) {
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        }
    }

    /**
     * AccessToken에서 provider(enum) 추출
     */
    public Optional<OauthServerType> extractOauthServerType(final String token) {
        try {
            String type = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token)
                    .getClaim(PROVIDER_CLAIM)
                    .asString();
            return Optional.of(OauthServerType.valueOf(type));
        } catch (Exception e) {
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        }
    }

    /**
     * JWT 유효성 검사
     */
    public void isTokenValid(final String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalidException("유효하지 않은 토큰");
        } catch (Exception e) {
            throw new RuntimeException("토큰 검증 중 런타임 오류 발생", e);
        }
    }

    /**
     * Redis에 RefreshToken 저장
     */
    public void updateRefreshToken(final String oauthServerId, final OauthServerType provider, final String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .oauthId(oauthServerId)
                .oauthServerType(provider)
                .refresh(refreshToken)
                .expiration(refreshTokenExpirationPeriod)
                .build();

        String key = oauthServerId + ":" + provider.name();
        log.info("Saving refreshToken to redis with key: {}", key);
        redisTemplate.opsForValue()
                .set(key, token, (long) refreshTokenExpirationPeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * RefreshToken이 blacklist인지 확인
     */
    public boolean isBlackList(final String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken) == null;
    }

    /**
     * RefreshToken 쿠키 생성
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

    public void deleteRefreshToken(String refreshToken) {
        Set<String> keys = redisTemplate.keys("*:*"); // "1234567890:KAKAO" 형식
        if (keys == null) return;

        for (String key : keys) {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj instanceof RefreshToken token && refreshToken.equals(token.getRefresh())) {
                redisTemplate.delete(key);
                break;
            }
        }
    }

}
