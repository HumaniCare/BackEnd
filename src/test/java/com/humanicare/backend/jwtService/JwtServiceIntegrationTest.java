package com.humanicare.backend.jwtService;

import com.humanicare.backend.domain.RefreshToken;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.oauth.OauthServerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtServiceIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void createAndVerifyAccessToken_shouldExtractOauthIdAndProviderCorrectly() {
        // given
        String oauthId = "1234567890";
        OauthServerType provider = OauthServerType.KAKAO;

        // when
        String token = jwtService.createAccessToken(oauthId, provider);

        // then
        assertTrue(jwtService.extractOauthId(token).isPresent());
        assertEquals(oauthId, jwtService.extractOauthId(token).get());

        assertTrue(jwtService.extractOauthServerType(token).isPresent());
        assertEquals(provider, jwtService.extractOauthServerType(token).get());
    }

    @Test
    public void updateRefreshToken_shouldSaveToRedisWithCorrectKey() {
        String oauthId = "1234567890";
        OauthServerType provider = OauthServerType.KAKAO;
        String refreshToken = jwtService.createRefreshToken();

        jwtService.updateRefreshToken(oauthId, provider, refreshToken);

        String redisKey = oauthId + ":" + provider.name();
        RefreshToken stored = (RefreshToken) redisTemplate.opsForValue().get(redisKey);

        assertNotNull(stored);
        assertEquals(refreshToken, stored.getRefresh());
    }
}
