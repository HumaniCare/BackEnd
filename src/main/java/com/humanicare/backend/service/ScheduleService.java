package com.humanicare.backend.service;

import com.humanicare.backend.domain.Schedule;
import com.humanicare.backend.dto.ScheduleDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    public List<Schedule> getAllSchedule(LocalDate date) {
        return null;
    }

    public Schedule getSchedule(Long id) {
        return Schedule.builder().build();
    }

    public void createSchedule(ScheduleDto.ScheduleDescriptionDto scheduleDto) {
    }

    public void updateSchedule(ScheduleDto.ScheduleDescriptionDto scheduleDto) {
    }

    public void deleteSchedule(ScheduleDto.ScheduleDescriptionDto scheduleDto) {
    }
}
