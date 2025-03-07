package com.humanicare.backend.oauth.google;

import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.oauth.OauthServerType;
import com.humanicare.backend.oauth.client.GoogleApiClient;
import com.humanicare.backend.oauth.client.OauthUserClient;
import com.humanicare.backend.oauth.dto.GoogleMemberResponse;
import com.humanicare.backend.oauth.dto.GoogleToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class GoogleUserClient implements OauthUserClient {

    private final GoogleApiClient googleApiClient;
    private final GoogleOauthConfig googleOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.GOOGLE;
    }

    @Override
    public User fetch(String authCode) {
        GoogleToken tokenInfo = googleApiClient.fetchToken(tokenRequestParams(authCode)); // (1)
        GoogleMemberResponse googleMemberResponse =
                googleApiClient.fetchMember("Bearer " + tokenInfo.accessToken());  // (2)
        return googleMemberResponse.toDomain();  // (3)
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleOauthConfig.clientId());
        params.add("redirect_uri", googleOauthConfig.redirectUri());
        params.add("code", authCode);
        params.add("client_secret", googleOauthConfig.clientSecret());
        return params;
    }
}
