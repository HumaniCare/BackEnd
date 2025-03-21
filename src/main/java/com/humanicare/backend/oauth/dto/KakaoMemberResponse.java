package com.humanicare.backend.oauth.dto;

import static com.humanicare.backend.oauth.OauthServerType.KAKAO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.humanicare.backend.domain.Role;
import com.humanicare.backend.domain.oauth.OauthId;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.oauth.InvitationCodeGenerator;
import com.humanicare.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * AccessToken을 사용해서 Kakao로부터 사용자의 정보를 받아오는 클래스
 */

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoMemberResponse(
        Long id,
        boolean hasSignedUp,
        LocalDateTime connectedAt,
        KakaoAccount kakaoAccount
) {

    private String generateInvitationCode() {
        // UUID로 랜덤 값 생성
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8); // 8자리 초대 코드
    }

    //아래의 값을 바꾸면 USER에 저장되는게 알아서 설정됨.
    public User toDomain(String invitationCode) {
        System.out.println(kakaoAccount.profile.nickname);
        return User.builder()
                .oauthId(new OauthId(String.valueOf(id), KAKAO))
                .name(kakaoAccount.profile.nickname)
                .email(kakaoAccount.email)
                .invitationCode(invitationCode)
                .role(Role.GUEST)
                .build();
    }

    @JsonNaming(SnakeCaseStrategy.class)
    public record KakaoAccount(
            boolean profileNeedsAgreement,
            boolean profileNicknameNeedsAgreement,
            boolean profileImageNeedsAgreement,
            Profile profile,
            boolean nameNeedsAgreement,
            String name,
            boolean emailNeedsAgreement,
            boolean isEmailValid,
            boolean isEmailVerified,
            String email,
            boolean ageRangeNeedsAgreement,
            String ageRange,
            boolean birthyearNeedsAgreement,
            String birthyear,
            boolean birthdayNeedsAgreement,
            String birthday,
            String birthdayType,
            boolean genderNeedsAgreement,
            String gender,
            boolean phoneNumberNeedsAgreement,
            String phoneNumber,
            boolean ciNeedsAgreement,
            String ci,
            LocalDateTime ciAuthenticatedAt
    ) {
    }

    @JsonNaming(SnakeCaseStrategy.class)
    public record Profile(
            String nickname,
            String thumbnailImageUrl,
            String profileImageUrl,
            boolean isDefaultImage
    ) {
    }
}
