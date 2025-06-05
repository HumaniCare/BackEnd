package com.humanicare.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class ReportDto {
    @JsonProperty("imageUrl")  // JSON 필드명과 일치
    private String imageUrl;

    @JsonProperty("report_text")  // snake_case 대응
    private String reportText;
}
