package com.humanicare.backend.apiPayload.code.status;


import com.humanicare.backend.apiPayload.code.BaseCode;
import com.humanicare.backend.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    //생성 2000, 조회 2001, 수정 2002, 삭제 2003

    _OK(HttpStatus.OK, "COMMON200", "성공"),

    //Reissue
    REISSUE_TOKEN(HttpStatus.OK, "REISSUE2000", "토큰 재발급 성공"),

    //User
    OAUTH_REDIRECT(HttpStatus.OK, "OAUTH2000", "소셜 로그인 리다이렉트 성공"),
    OAUTH_LOGIN(HttpStatus.OK, "OAUTH2001", "소셜 로그인 성공"),
    SIGN_OUT_USER(HttpStatus.OK, "USER2000", "회원 탈퇴 성공"),

    //BasicSchedule
    SAVE_BASIC_SCHEDULE(HttpStatus.OK, "BASICSCHEDULE2000", "기본 일정 생성 성공"),
    GET_BASIC_SCHEDULE(HttpStatus.OK, "BASICSCHEDULE2001", "기본 일정 조회 성공"),
    PUT_BASIC_SCHEDULE(HttpStatus.OK, "BASICSCHEDULE2002", "기본 일정 수정 성공"),
    DELETE_BASIC_SCHEDULE(HttpStatus.OK, "BASICSCHDULE2003", "기본 일정 삭제 성공"),

    //Schedule
    SAVE_SCHEDULE(HttpStatus.OK, "SCHEDULE2000", "특수 일정 생성 성공"),
    GET_SCHEDULE(HttpStatus.OK, "SCHEDULE2001", "특수 일정 조회 성공"),
    PUT_SCHEDULE(HttpStatus.OK, "SCHEDULE2002", "특수 일정 수정 성공"),
    DELETE_SCHEDULE(HttpStatus.OK, "SCHDULE2003", "특수 일정 삭제 성공");


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
