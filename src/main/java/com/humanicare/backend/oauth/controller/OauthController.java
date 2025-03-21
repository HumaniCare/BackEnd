package com.humanicare.backend.oauth.controller;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.domain.Role;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.oauth.OauthServerType;
import com.humanicare.backend.oauth.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring/oauth")
@Slf4j
/**
 * @Pathvariable을 통해 /oauth/kakao 등의 요청에서 kakao 부분을 oauthServerType으로 변환한다 (converter 이용)
 * 사용자가 프론트에서 /oauth/kakao를 통해 접속하면 밑 controller를 통한다.
 * oauthService를 통해 AuthCode를 받아오기 위한 URL을 생성하고 이 URL로 사용자를 Redirect시킨다.
 */

public class OauthController {

    private final OauthService oauthService;

    //인가 코드 받아오기
    @SneakyThrows
    @GetMapping("/{oauthServerType}")
    ApiResponse<Void> redirectAuthCodeRequestUrl(
            @PathVariable("oauthServerType") final OauthServerType oauthServerType,
            final HttpServletResponse response
    ) {
        String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);
        response.sendRedirect(redirectUrl);
        return ApiResponse.ofNoting(SuccessStatus.OAUTH_REDIRECT);
    }

    //위의 인가 코드 받아오는게 완료하면 자동으로 아래의 login 실행
    @GetMapping("/login/{oauthServerType}")
    ApiResponse<Role> login(
            @PathVariable("oauthServerType") final OauthServerType oauthServerType,
            @RequestParam("code") final String code,
            final HttpServletResponse response
    ) {
        log.info("🔹 Received authCode: {}", code);
        User user = oauthService.login(response, oauthServerType, code);
        return ApiResponse.of(SuccessStatus.OAUTH_LOGIN, user.getRole());
    }
}
