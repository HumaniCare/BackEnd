package com.humanicare.backend.controller;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.converter.BasicScheduleConverter;
import com.humanicare.backend.converter.ScheduleConverter;
import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.Schedule;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.dto.ScheduleDto;
import com.humanicare.backend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_PREFIX;
import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
public class ScheduleController {

    private final ScheduleService scheduleService;

    //후에 쿼리로 날짜 전해줄거임.
    @GetMapping("/all-schedules")
    @Operation(summary = "모든 특수 일정 가져오기")
    public ApiResponse<List<ScheduleDto.ScheduleSimpleDto>> getAllSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                                @RequestParam LocalDate date) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        List<Schedule> schedules = scheduleService.getAllSchedule(accessToken, date);
        return ApiResponse.of(SuccessStatus.GET_SCHEDULE,
                ScheduleConverter.toScheduleDtoList(schedules));
    }

    @GetMapping("/schedules")
    @Operation(summary = "하나의 특수 일정 가져오기")
    public ApiResponse<ScheduleDto.ScheduleDescriptionDto> getSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                               @RequestParam("scheduleId") Long id) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        Schedule schedule = scheduleService.getSchedule(accessToken, id);
        return ApiResponse.of(SuccessStatus.GET_SCHEDULE,
                ScheduleConverter.toScheduleDto(schedule));
    }

    @PostMapping("/schedules")
    @Operation(summary = "특수 일정 생성하기")
    public ApiResponse<Void> createSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                                  @RequestBody final ScheduleDto.ScheduleDescriptionDto scheduleDto) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        scheduleService.createSchedule(accessToken, scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.SAVE_SCHEDULE);
    }

    @PutMapping("/schedules")
    @Operation(summary = "특수 일정 수정하기")
    public ApiResponse<Void> updateSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                    @RequestBody final ScheduleDto.ScheduleDescriptionDto scheduleDto,
                                                    @RequestParam("scheduleId") Long id) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        scheduleService.updateSchedule(accessToken, scheduleDto, id);
        return ApiResponse.ofNoting(SuccessStatus.PUT_SCHEDULE);
    }

    @DeleteMapping("/schedules")
    @Operation(summary = "특수 일정 삭제하기")
    public ApiResponse<Void> deleteSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                    @RequestBody final ScheduleDto.ScheduleDescriptionDto scheduleDto,
                                                    @RequestParam("scheduleId") Long id) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        scheduleService.deleteSchedule(accessToken, scheduleDto, id);
        return ApiResponse.ofNoting(SuccessStatus.DELETE_SCHEDULE);
    }

}
