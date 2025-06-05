package com.humanicare.backend.controller;

import com.humanicare.backend.apiPayload.ApiResponse;
import com.humanicare.backend.apiPayload.code.status.SuccessStatus;
import com.humanicare.backend.converter.ReportConverter;
import com.humanicare.backend.domain.Report;
import com.humanicare.backend.dto.ReportDto;
import com.humanicare.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_PREFIX;
import static com.humanicare.backend.constant.Constants.ACCESS_TOKEN_REPLACEMENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/reports")
    @Operation(summary = "감정 일기 확인하기")
    public ApiResponse<ReportDto> getDiary(@RequestHeader("Authorization") final String authorizationHeader) {
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        ReportDto report = reportService.getReport(accessToken);
        return ApiResponse.of(SuccessStatus.GET_REPORT,
                report);
    }

    @PostMapping("/report")
    @Operation(summary="report 저장")
    public ApiResponse<Void> createReport(@RequestHeader("Authorization") final String authorizationHeader,
                                          @RequestBody ReportDto reportDto) {
        log.info("Create BasicSchedule");
        String accessToken = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, ACCESS_TOKEN_REPLACEMENT);
        reportService.createReport(accessToken, reportDto);
        return ApiResponse.ofNoting(SuccessStatus.SAVE_REPORT);
    }
}
