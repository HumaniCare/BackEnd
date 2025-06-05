package com.humanicare.backend.service;

import com.humanicare.backend.converter.ReportConverter;
import com.humanicare.backend.domain.Report;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.ReportDto;
import com.humanicare.backend.repository.ReportRepository;
import com.humanicare.backend.service.user.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final UserCheckService userCheckService;
    private final ReportRepository reportRepository;

    public void createReport(String accessToken, ReportDto reportDto) {
        User user = userCheckService.getUserByToken(accessToken);
        Report report = ReportConverter.toReport(user, reportDto);
        log.info("저장된 report: {}", report);
        reportRepository.save(report);
    }
}
