package com.humanicare.backend.oauth.dto;

import static com.humanicare.backend.oauth.OauthServerType.NAVER;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.humanicare.backend.domain.Role;
import com.humanicare.backend.domain.oauth.OauthId;
import com.humanicare.backend.domain.oauth.User;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverMemberResponse(
        String resultcode,
        String message,
        Response response
) {

    public User toDomain() {
        System.out.println(response.name);
        return User.builder()
                .oauthId(new OauthId(String.valueOf(response.id), NAVER))
                .name(response.name)
                .email(response.email)
                .role(Role.GUEST)
                .build();
    }

    public LocalDate getBirthDate(String birthyear, String birthday) {
        if (birthyear != null && birthday != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(birthyear + "-" + birthday, formatter);
        }
        return null;
    }

    public Integer calculateAge(String birthyear) {
        if (birthyear != null && !birthyear.isEmpty()) {
            try {
                int birthYear = Integer.parseInt(birthyear);
                int currentYear = Year.now().getValue();
                return currentYear - birthYear + 1;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Response(
            String id,
            String nickname,
            String name,
            String email,
            String gender,
            String age,
            String birthday,
            String profileImage,
            String birthyear,
            String mobile
    ) {
    }
}
