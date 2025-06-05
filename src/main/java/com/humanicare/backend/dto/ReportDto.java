package com.humanicare.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("report_text")
    private String reportText;
}
