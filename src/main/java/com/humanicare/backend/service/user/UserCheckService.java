package com.humanicare.backend.service.user;

import com.humanicare.backend.apiPayload.code.status.ErrorStatus;
import com.humanicare.backend.apiPayload.exception.handler.UserHandler;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCheckService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public User getUserByToken(final String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        User user = userRepository.findByEmail(email).orElse(null);
        validateUserIsNotNull(user);
        return user;
    }

    private void validateUserIsNotNull(final User user) {
        if (user == null) {
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        }
    }
}
