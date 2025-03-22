package com.humanicare.backend.controller;

import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_PREFIX;
import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.service.user.UserCheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
public class UserController {

    private final UserCheckService userCheckService;

    @PostMapping("/login")
    @Operation(summary = "스웨거 용 로그인")
    public ApiResponse<Void> login(@RequestHeader("Authorization") final String authorizationHeader) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        userCheckService.getUserByToken(accessToken);
        return ApiResponse.ofNoting(SuccessStatus._OK);
    }

    @GetMapping("/invitation")
    @Operation(summary = "초대코드 확인")
    public ApiResponse<String> getInvitationCode(@RequestHeader("Authorization") final String authorizationHeader) {
        String invitationCode = "1234"; //DTO로 만들기
//        userService.getInvitationCode();
        return ApiResponse.onSuccess(invitationCode);
    }

    @PostMapping("/invitation")
    @Operation(summary = "초대코드 입력")
    public ApiResponse<Void> inputInvitationCode(@RequestHeader("Authorization") final String authorizationHeader) {
        return ApiResponse.ofNoting(SuccessStatus._OK);
    }
}
