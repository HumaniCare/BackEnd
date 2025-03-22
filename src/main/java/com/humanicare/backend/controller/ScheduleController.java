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
        List<Schedule> schedules = scheduleService.getAllSchedule(date);
        return ApiResponse.of(SuccessStatus.GET_SCHEDULE,
                ScheduleConverter.toScheduleDtoList(schedules));
    }

    @GetMapping("/schedules/{id}")
    @Operation(summary = "하나의 특수 일정 가져오기")
    public ApiResponse<ScheduleDto.ScheduleDescriptionDto> getSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                               @PathVariable Long id) {
        Schedule schedule = scheduleService.getSchedule(id);
        return ApiResponse.of(SuccessStatus.GET_SCHEDULE,
                ScheduleConverter.toScheduleDto(schedule));
    }

    @PostMapping("/schedules")
    @Operation(summary = "특수 일정 생성하기")
    public ApiResponse<Void> createSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                                                  @RequestBody final ScheduleDto.ScheduleDescriptionDto scheduleDto) {
        scheduleService.createSchedule(scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.SAVE_SCHEDULE);
    }

    @PutMapping("/schedules")
    @Operation(summary = "특수 일정 수정하기")
    public ApiResponse<Void> updateSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                    @RequestBody final ScheduleDto.ScheduleDescriptionDto scheduleDto) {
        scheduleService.updateSchedule(scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.PUT_SCHEDULE);
    }

    @DeleteMapping("/schedules")
    @Operation(summary = "특수 일정 삭제하기")
    public ApiResponse<Void> deleteSpecificSchedule(@RequestHeader("Authorization") final String authorizationHeader,
                                                    @RequestBody final ScheduleDto.ScheduleDescriptionDto scheduleDto) {
        scheduleService.deleteSchedule(scheduleDto);
        return ApiResponse.ofNoting(SuccessStatus.DELETE_SCHEDULE);
    }

}
