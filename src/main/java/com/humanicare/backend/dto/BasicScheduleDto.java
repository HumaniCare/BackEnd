package com.humanicare.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

public class BasicScheduleDto {

    @Getter
    @Builder
    public static class ScheduleDto {
        private Long id;
        private String scheduleTitle;
        private LocalTime startTime;

        public ScheduleDto() {
        }

        public ScheduleDto(Long id, String scheduleTitle, LocalTime startTime) {
            this.id = id;
            this.scheduleTitle = scheduleTitle;
            this.startTime = startTime;
        }
    }
}
