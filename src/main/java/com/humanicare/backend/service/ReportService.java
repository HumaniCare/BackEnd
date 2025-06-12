package com.humanicare.backend.service;

import com.humanicare.backend.converter.ReportConverter;
import com.humanicare.backend.domain.Report;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.ReportDto;
import com.humanicare.backend.repository.ReportRepository;
import com.humanicare.backend.repository.UserRepository;
import com.humanicare.backend.service.user.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final UserCheckService userCheckService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final SendMessage sendMessage;

    public void createReport(ReportDto reportDto) throws CoolsmsException {
        log.info("Creating report for report: {}", reportDto);
        User user = userRepository.findById(3L).orElseThrow(NoSuchElementException::new);
        Report report = ReportConverter.toReport(user, reportDto);
        log.info("저장된 report: {}", report);
        reportRepository.save(report);
        JSONObject result = sendMessage.sendSms("report가 update되었습니다. www.humanicare.store로 들어가서 확인하세요.");
    }

    public ReportDto getReport(String accessToken) {
        User user = userCheckService.getUserByToken(accessToken);
        Report latestReport = reportRepository.findTopByUserOrderByIdDesc(user)
                .orElseThrow(() -> new NoSuchElementException("최근 리포트가 없습니다."));
        return ReportConverter.toReportDto(latestReport);
    }
}
