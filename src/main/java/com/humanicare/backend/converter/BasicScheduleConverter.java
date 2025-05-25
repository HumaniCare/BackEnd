package com.humanicare.backend.converter;

import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.BasicScheduleDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicScheduleConverter {

    public static List<BasicScheduleDto.ScheduleDto> toScheduleDtoList(List<BasicSchedule> schedules) {
        return schedules.stream()
                .map(schedule -> BasicScheduleDto.ScheduleDto.builder()
                        .id(schedule.getId())
                        .scheduleTitle(schedule.getScheduleTitle())
                        .startTime(schedule.getStartTime())
                        .days(schedule.getDays())
                        .build())
                .collect(Collectors.toList());
    }

    public static BasicSchedule toBasicSchedule(final User user, final BasicScheduleDto.ScheduleDto dto) {
        return BasicSchedule.builder()
                .scheduleTitle(dto.getScheduleTitle())
                .startTime(dto.getStartTime())
                .days(dto.getDays())
                .user(user)
                .build();
    }

    public static BasicScheduleDto.ScheduleDto toBasicScheduleDto(final BasicSchedule schedule) {
        return BasicScheduleDto.ScheduleDto.builder()
                .id(schedule.getId())
                .scheduleTitle(schedule.getScheduleTitle())
                .startTime(schedule.getStartTime())
                .days(schedule.getDays())
                .url(schedule.getUrl())
                .build();
    }
}
