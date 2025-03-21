package com.humanicare.backend.domain.oauth;

import com.humanicare.backend.domain.BaseEntity;
import com.humanicare.backend.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "oauth_id_unique",
                        columnNames = {
                                "oauth_server_id",
                                "oauth_server"
                        }
                ),
        }
)
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;
    private String invitationCode;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private OauthId oauthId;

    @Column
    private String voiceUrl;

    public void updateVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public void updateRole() {
        this.role = Role.USER;
    }

}
