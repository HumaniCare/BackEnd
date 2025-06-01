package com.humanicare.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.converter.BasicScheduleConverter;
import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.jwt.service.JwtService;
import com.humanicare.backend.service.BasicScheduleService;
import com.humanicare.backend.service.UrlService;
import com.humanicare.backend.service.user.UserCheckService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_PREFIX;
import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
@Slf4j
public class BasicScheduleController {

    private final BasicScheduleService basicScheduleService;

    @GetMapping("/all-basic-schedules")
    @Operation(summary = "모든 기본 일정 가져오기")
    public ApiResponse<List<BasicScheduleDto.ScheduleDto>> getAllBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        List<BasicSchedule> schedules = basicScheduleService.getAllSchedule(accessToken);
        return ApiResponse.of(SuccessStatus.GET_BASIC_SCHEDULE,
                BasicScheduleConverter.toScheduleDtoList(schedules));
    }

    @GetMapping("/basic-schedules")
    @Operation(summary = "하나의 기본 일정 가져오기")
    public ApiResponse<BasicScheduleDto.ScheduleDto> getBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                      @RequestParam("scheduleId") final Long id) {
        BasicSchedule schedule = basicScheduleService.getSchedule(id);
        return ApiResponse.of(SuccessStatus.GET_BASIC_SCHEDULE,
                BasicScheduleConverter.toBasicScheduleDto(schedule));
    }

    @PostMapping("/basic-schedules")
    @Operation(summary = "기본 일정 생성하기", description = "기본적으로 여러 개를 생성할 수 있게 하자.")
    public ApiResponse<Void> createBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                 @RequestBody List<BasicScheduleDto.ScheduleDto> scheduleDtos) throws CoolsmsException {
        log.info("Create BasicSchedule");
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        basicScheduleService.createSchedule(accessToken, scheduleDtos);
        return ApiResponse.ofNoting(SuccessStatus.SAVE_BASIC_SCHEDULE);
    }

//    @PutMapping("/basic-schedules")
//    @Operation(summary = "기본 일정 수정하기")
//    public ApiResponse<Void> updateBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
//                                                 @RequestBody BasicScheduleDto.ScheduleDto scheduleDto, @RequestParam("scheduleId") final Long id) {
//        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
//        basicScheduleService.updateSchedule(accessToken, scheduleDto, id);
//        return ApiResponse.ofNoting(SuccessStatus.PUT_BASIC_SCHEDULE);
//    }
//
//    @DeleteMapping("/basic-schedules")
//    @Operation(summary = "기본 일정 삭제하기")
//    public ApiResponse<Void> deleteBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
//                                                 @RequestParam("ScheduleId") final Long id) {
//        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
//        basicScheduleService.deleteSchedule(accessToken, id);
//        return ApiResponse.ofNoting(SuccessStatus.DELETE_BASIC_SCHEDULE);
//    }
}
