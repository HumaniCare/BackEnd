package com.humanicare.backend.oauth.dto;

import static com.humanicare.backend.oauth.OauthServerType.GOOGLE;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.humanicare.backend.domain.Role;
import com.humanicare.backend.domain.oauth.OauthId;
import com.humanicare.backend.domain.oauth.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleMemberResponse(
        String id,
        String email,
        boolean verified_email,
        String name,
        String given_name,
        String family_name,
        String picture,
        String locale
) {

    public User toDomain() {
        System.out.println(name);
        return User.builder()
                .oauthId(new OauthId(id, GOOGLE))
                .email(email)
                .name(name)
                .role(Role.GUEST)
                .build();
    }
}
