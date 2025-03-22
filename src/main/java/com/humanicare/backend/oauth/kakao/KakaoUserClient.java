package com.humanicare.backend.oauth.kakao;

import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.oauth.InvitationCodeGenerator;
import com.humanicare.backend.oauth.OauthServerType;
import com.humanicare.backend.oauth.client.KakaoApiClient;
import com.humanicare.backend.oauth.client.OauthUserClient;
import com.humanicare.backend.oauth.dto.KakaoMemberResponse;
import com.humanicare.backend.oauth.dto.KakaoToken;
import com.humanicare.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor

/**
 * fetch 메소드에 대한 설명
 * (1) - authCode를 통해 token을 가져온다
 * (2) - token을 통해 회원 정보를 받아온다
 * (3) - 회원정보를 OauthUser 객체로 변환한다.
 */

public class KakaoUserClient implements OauthUserClient {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoOauthConfig kakaoOauthConfig;
    private final InvitationCodeGenerator invitationCodeGenerator;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.KAKAO;
    }

    @Override
    public User fetch(String authCode) {
        KakaoToken tokenInfo = kakaoApiClient.fetchToken(tokenRequestParams(authCode)); // (1)
        KakaoMemberResponse kakaoMemberResponse = kakaoApiClient.fetchMember(
                "Bearer " + tokenInfo.accessToken());  // (2)
        return kakaoMemberResponse.toDomain(invitationCodeGenerator.generateUniqueInvitationCode());  // (3)
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauthConfig.clientId());
        params.add("redirect_uri", kakaoOauthConfig.redirectUri());
        params.add("code", authCode);
        params.add("client_secret", kakaoOauthConfig.clientSecret());
        return params;
    }
}
