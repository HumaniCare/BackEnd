package com.humanicare.backend.controller;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.converter.BasicScheduleConverter;
import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.service.BasicScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_PREFIX;
import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
public class BasicScheduleController {

    private final BasicScheduleService basicScheduleService;

    @GetMapping("/all-basic-schedules")
    @Operation(summary = "모든 기본 일정 가져오기")
    public ApiResponse<List<BasicScheduleDto.ScheduleDto>> getAllBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader) {
        List<BasicSchedule> schedules = basicScheduleService.getAllSchedule();
        return ApiResponse.of(SuccessStatus.GET_BASIC_SCHEDULE,
                BasicScheduleConverter.toScheduleDtoList(schedules));
    }

    @GetMapping("/basic-schedules/{id}")
    @Operation(summary = "하나의 기본 일정 가져오기")
    public ApiResponse<BasicScheduleDto.ScheduleDto> getBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                      @PathVariable Long id) {
        BasicSchedule schedule = basicScheduleService.getSchedule(id);
        return ApiResponse.of(SuccessStatus.GET_BASIC_SCHEDULE,
                BasicScheduleConverter.toBasicScheduleDto(schedule));
    }

    @PostMapping("/basic-schedules")
    @Operation(summary = "기본 일정 생성하기")
    public ApiResponse<Void> createBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                 @RequestBody BasicScheduleDto.ScheduleDto scheduleDto) {
        basicScheduleService.createSchedule(scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.SAVE_BASIC_SCHEDULE);
    }

    @PutMapping("/basic-schedules")
    @Operation(summary = "기본 일정 수정하기")
    public ApiResponse<BasicScheduleDto.ScheduleDto> updateBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                 @RequestBody BasicScheduleDto.ScheduleDto scheduleDto) {
        basicScheduleService.updateSchedule(scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.PUT_BASIC_SCHEDULE);
    }

    @DeleteMapping("/basic-schedules")
    @Operation(summary = "기본 일정 삭제하기")
    public ApiResponse<Void> deleteBasicSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                 @RequestBody BasicScheduleDto.ScheduleDto scheduleDto) {
        basicScheduleService.deleteSchedule(scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.DELETE_BASIC_SCHEDULE);
    }
}
