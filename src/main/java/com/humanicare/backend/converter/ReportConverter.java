package com.humanicare.backend.converter;

import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.Report;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.dto.ReportDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportConverter {
    public static Report toReport(final User user, final ReportDto dto) {
        return Report.builder()
                .imageUrl(dto.getImageUrl())
                .reportText(dto.getReportText())
                .date(LocalDate.now())
                .user(user)
                .build();
    }

    public static ReportDto toReportDto(final Report report) {
        return ReportDto.builder()
                .imageUrl(report.getImageUrl())
                .reportText(report.getReportText())
                .build();
    }
}
