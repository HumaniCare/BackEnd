package com.humanicare.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

public class ScheduleDto {

    //전체를 가져올 때 사용
    @Getter
    @Builder
    public static class ScheduleSimpleDto {
        private Long id;
        private String scheduleTitle;
        private LocalTime startTime;

        public ScheduleSimpleDto() {
        }

        public ScheduleSimpleDto(Long id, String scheduleTitle, LocalTime startTime) {
            this.id = id;
            this.scheduleTitle = scheduleTitle;
            this.startTime = startTime;
        }
    }

    //하나하나 세부적으로 볼 때 사용
    @Getter
    @Builder
    public static class ScheduleDescriptionDto {
        private Long id;
        private String scheduleTitle;
        private LocalTime startTime;
        private String description;

        public ScheduleDescriptionDto() {
        }

        public ScheduleDescriptionDto(Long id, String scheduleTitle, LocalTime startTime, String description) {
            this.id = id;
            this.scheduleTitle = scheduleTitle;
            this.startTime = startTime;
            this.description = description;
        }
    }
}
