package com.humanicare.backend.repository;

import com.humanicare.backend.domain.oauth.OauthId;
import com.humanicare.backend.domain.oauth.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    Optional<User> findByOauthId(final OauthId oauthId);

    boolean existsByInvitationCode(String code);
}
