package com.humanicare.backend.oauth.service;

import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.oauth.AuthCodeRequestUrlProviderComposite;
import com.humanicare.backend.oauth.OauthServerType;
import com.humanicare.backend.oauth.client.OauthUserClientComposite;
import com.humanicare.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
/**
 * OauthServerType을 받아서 해당 인증 서버에서 Auth Code를 받아오기 위한 URL 주소 생성
 * 로그인
 */

public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthUserClientComposite oauthUserClientComposite;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public String getAuthCodeRequestUrl(final OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    public User login(final HttpServletResponse response, final OauthServerType oauthServerType, final String authCode) {
        User user = oauthUserClientComposite.fetch(oauthServerType, authCode);
        User saved = userRepository.findByOauthId(user.getOauthId()).orElseGet(() -> userRepository.save(user));

        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//        jwtService.updateRefreshToken(user.getEmail(), refreshToken);

        log.info("사용자 로그인 완료, email : {}", user.getEmail());
        return saved;
    }
}
