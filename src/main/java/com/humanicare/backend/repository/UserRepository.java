package com.humanicare.backend.repository;

import com.humanicare.backend.domain.oauth.OauthId;
import com.humanicare.backend.domain.oauth.User;
import java.util.Optional;

import com.humanicare.backend.oauth.OauthServerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    Optional<User> findByOauthId(final OauthId oauthId);

    Optional<User> findByOauthId_OauthServerIdAndOauthId_OauthServerType(String oauthServerId, OauthServerType oauthServerType);
}
