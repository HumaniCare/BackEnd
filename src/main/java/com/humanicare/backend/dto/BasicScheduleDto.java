package com.humanicare.backend.dto;

import com.humanicare.backend.domain.Day;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

public class BasicScheduleDto {

    @Getter
    @Builder
    public static class ScheduleDto {
        @Setter
        private Long id;
        private String scheduleTitle;
        private LocalTime startTime;
        private List<Day> days;

        public ScheduleDto() {
        }

        public ScheduleDto(Long id, String scheduleTitle, LocalTime startTime, List<Day> days) {
            this.id = id;
            this.scheduleTitle = scheduleTitle;
            this.startTime = startTime;
            this.days = days;
        }
    }
}
