package com.humanicare.backend.service.user;

import com.humanicare.backend.domain.oauth.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUpdateService {

    @Transactional
    public void updateUserVoice(User user, String voiceUrl) {
        user.updateVoiceUrl(voiceUrl);
    }

//    @Transactional
//    public void updateAlias(User user, String alias) {
//        user.updateAlias(alias);
//    }
}
