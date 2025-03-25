package com.humanicare.backend.domain.oauth;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import com.humanicare.backend.oauth.OauthServerType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)

/**
 * 특정 인증 서버의 식별자 값을 의미하는 oauthServerId와 이를 제공하는 서비스를 묶은 클래스
 */

public class OauthId {

    @Column(nullable = false, name = "oauth_server_id")
    private String oauthServerId;

    @Enumerated(STRING)
    @Column(nullable = false, name = "oauth_server")
    private OauthServerType oauthServerType;

    public String oauthServerId() {
        return oauthServerId;
    }

    public OauthServerType oauthServer() {
        return oauthServerType;
    }

    //respository에서 findBy를 할 때 객체 비교를 정확하게 수행할 수 있다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OauthId that)) return false;
        return Objects.equals(oauthServerId, that.oauthServerId)
                && oauthServerType == that.oauthServerType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oauthServerId, oauthServerType);
    }
}
