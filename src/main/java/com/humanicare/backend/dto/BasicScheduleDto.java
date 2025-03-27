package com.humanicare.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

public class BasicScheduleDto {

    @Getter
    @Builder
    public static class ScheduleDto {
        @Setter
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
