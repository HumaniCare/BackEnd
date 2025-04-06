package com.humanicare.backend.service.user;

import com.humanicare.backend.apiPayload.code.status.ErrorStatus;
import com.humanicare.backend.apiPayload.exception.handler.BasicScheduleHandler;
import com.humanicare.backend.apiPayload.exception.handler.UserHandler;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {
    private final UserRepository userRepository;

    @Transactional
    public void updateUserRole(User user) {
        log.info("update user role");
        User original = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserHandler(ErrorStatus._USER_NOT_FOUND));
        original.updateRole();
    }
}
