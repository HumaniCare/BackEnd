package com.humanicare.backend.oauth.client;

import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.oauth.OauthServerType;

/**
 * AuthCode를 통해 OauthMember 객체 생성 (회원 정보 조회)
 */

public interface OauthUserClient {

    OauthServerType supportServer();

    User fetch(String code);
}
