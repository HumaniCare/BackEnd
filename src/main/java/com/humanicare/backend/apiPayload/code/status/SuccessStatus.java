package com.humanicare.backend.apiPayload.code.status;


import com.humanicare.backend.apiPayload.code.BaseCode;
import com.humanicare.backend.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK, "COMMON200", "성공"),

    //Reissue
    REISSUE_TOKEN(HttpStatus.OK, "REISSUE2000", "토큰 재발급 성공"),

    //User
    OAUTH_REDIRECT(HttpStatus.OK, "OAUTH2000", "소셜 로그인 리다이렉트 성공"),
    OAUTH_LOGIN(HttpStatus.OK, "OAUTH2001", "소셜 로그인 성공"),
    SIGN_OUT_USER(HttpStatus.OK, "USER2000", "회원 탈퇴 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
