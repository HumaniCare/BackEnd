package com.humanicare.backend.controller;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.converter.DiaryConverter;
import com.humanicare.backend.converter.ScheduleConverter;
import com.humanicare.backend.domain.Diary;
import com.humanicare.backend.domain.Schedule;
import com.humanicare.backend.dto.DiaryDto;
import com.humanicare.backend.dto.ScheduleDto;
import com.humanicare.backend.service.DiaryService;
import com.humanicare.backend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/emotions")
    @Operation(summary = "감정 일기 확인하기")
    public ApiResponse<DiaryDto.DiaryFullDto> getDiary(@RequestHeader("Authorization") final String authorizationHeader,
                                                        @RequestParam LocalDate date) {
        Diary diary = diaryService.getDiary(date);
        return ApiResponse.of(SuccessStatus.GET_DIARY,
                DiaryConverter.toDiaryDto(diary));
    }
}
