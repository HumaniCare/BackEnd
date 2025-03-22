package com.humanicare.backend.service;

import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.dto.BasicScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BasicScheduleService {

    public List<BasicSchedule> getAllSchedule() {
        return new ArrayList<>();
    }


    public BasicSchedule getSchedule(final Long id) {
        return BasicSchedule.builder().build();
    }

    public void createSchedule(BasicScheduleDto.ScheduleDto scheduleDto) {

    }

    public void updateSchedule(BasicScheduleDto.ScheduleDto scheduleDto) {

    }

    public void deleteSchedule(BasicScheduleDto.ScheduleDto scheduleDto) {

    }
}
