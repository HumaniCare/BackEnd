package com.humanicare.backend.converter;

import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.Schedule;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.dto.ScheduleDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleConverter {

    public static List<ScheduleDto.ScheduleSimpleDto> toScheduleDtoList(List<Schedule> schedules) {
        return schedules.stream()
                .map(schedule -> new ScheduleDto.ScheduleSimpleDto(
                        schedule.getId(),
                        schedule.getScheduleTitle(),
                        schedule.getStartTime()
                ))
                .collect(Collectors.toList());
    }

    public static Schedule toSchedule(final User user, final ScheduleDto.ScheduleDescriptionDto dto) {
        return Schedule.builder()
                .id(dto.getId())
                .scheduleTitle(dto.getScheduleTitle())
                .startTime(dto.getStartTime())
                .description(dto.getDescription())
                .user(user)
                .build();
    }

    public static ScheduleDto.ScheduleDescriptionDto toScheduleDto(final Schedule schedule) {
        return ScheduleDto.ScheduleDescriptionDto.builder()
                .id(schedule.getId())
                .scheduleTitle(schedule.getScheduleTitle())
                .startTime(schedule.getStartTime())
                .description(schedule.getDescription())
                .build();
    }
}
