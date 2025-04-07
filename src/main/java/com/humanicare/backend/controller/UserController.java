package com.humanicare.backend.controller;

import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_PREFIX;
import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.service.user.UserCheckService;
import com.humanicare.backend.service.user.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
@Slf4j
public class UserController {

    private final UserCheckService userCheckService;
    private final UserRoleService userRoleService;

    @PostMapping("/login")
    @Operation(summary = "스웨거 용 로그인")
    public ApiResponse<Void> login(@RequestHeader("Authorization") final String authorizationHeader) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        userCheckService.getUserByToken(accessToken);
        return ApiResponse.ofNoting(SuccessStatus._OK);
    }

    @PostMapping("/logout")
    @Operation(summary="로그 아웃")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") final String authorizationHeader) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        User user = userCheckService.getUserByToken(accessToken);
        log.info("user: {}", user);
        userRoleService.updateUserRole(user);
        log.info("user: {}", user);
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
