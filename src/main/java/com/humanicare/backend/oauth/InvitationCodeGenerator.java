package com.humanicare.backend.oauth;

import com.humanicare.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class InvitationCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8; // 초대 코드 길이

    private final UserRepository userRepository;

    @Autowired
    public InvitationCodeGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUniqueInvitationCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (userRepository.existsByInvitationCode(code)); // 초대 코드가 이미 존재하면 새로운 코드 생성
        return code;
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
