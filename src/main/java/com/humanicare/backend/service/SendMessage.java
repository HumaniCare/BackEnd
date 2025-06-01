package com.humanicare.backend.service;

import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.service.user.UserCheckService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class SendMessage {

    private final UserCheckService userCheckService;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    public JSONObject sendSms(String accessToken, String text) throws CoolsmsException {
        System.out.println("메세지 전송 시작");
        User user = userCheckService.getUserByToken(accessToken);

        Message coolsms = new Message(apiKey, apiSecret);
        HashMap<String, String> params = new HashMap<>();
        params.put("to", user.getPhoneNum());           // 수신번호 (ex: "01012345678")
        params.put("from", "01093333611");       // 발신번호 (ex: "01087654321")
        params.put("type", "SMS");      // 문자 타입 (SMS, LMS, MMS 등)
        params.put("text", text);       // 문자 내용

        try {
            JSONObject result = coolsms.send(params);
            System.out.println(result.toJSONString());
            return result;
        } catch (CoolsmsException e) {
            System.out.println("Error code: " + e.getCode());
            System.out.println("Message: " + e.getMessage());
            throw e;
        }
    }
}
