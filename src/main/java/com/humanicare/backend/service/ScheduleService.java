package com.humanicare.backend.service;

import com.humanicare.backend.apiPayload.code.status.ErrorStatus;
import com.humanicare.backend.apiPayload.exception.handler.BasicScheduleHandler;
import com.humanicare.backend.apiPayload.exception.handler.ScheduleHandler;
import com.humanicare.backend.converter.BasicScheduleConverter;
import com.humanicare.backend.converter.ScheduleConverter;
import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.Schedule;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.ScheduleDto;
import com.humanicare.backend.repository.ScheduleRepository;
import com.humanicare.backend.service.user.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final UserCheckService userCheckService;
    private final ScheduleRepository scheduleRepository;

    //Schedule의 User id와 현재 로그인된 User의 id가 일치하지 않으면 Forbidden
    private void checkValidUser(Schedule schedule, User currentUser) {
        if (!schedule.getUser().getId().equals(currentUser.getId())) {
            throw new BasicScheduleHandler(ErrorStatus._FORBIDDEN);
        }
    }

    public List<Schedule> getAllSchedule(String accessToken, LocalDate date) {
        User user = userCheckService.getUserByToken(accessToken);
        return scheduleRepository
                .findByUserAndStartTimeBetween(user, date.atStartOfDay(), date.atTime(LocalTime.MAX));
    }

    public Schedule getSchedule(String accessToken, Long id) {
        User user = userCheckService.getUserByToken(accessToken);
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleHandler(ErrorStatus._SCHEDULE_NOT_FOUND));

        checkValidUser(schedule, user);

        return schedule;
    }

    public void createSchedule(String accessToken, ScheduleDto.ScheduleDescriptionDto scheduleDto) {
        User user = userCheckService.getUserByToken(accessToken);
        scheduleRepository.save(ScheduleConverter.toSchedule(user, scheduleDto));
    }

    @Transactional
    public void updateSchedule(String accessToken, ScheduleDto.ScheduleDescriptionDto scheduleDto, Long id) {
        User user = userCheckService.getUserByToken(accessToken);
        Schedule original = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleHandler(ErrorStatus._SCHEDULE_NOT_FOUND)); // ② 기존 일정 조회

        checkValidUser(original, user);
        original.changeSchedule(scheduleDto.getScheduleTitle(), scheduleDto.getStartTime(), scheduleDto.getDescription());
    }

    public void deleteSchedule(String accessToken, ScheduleDto.ScheduleDescriptionDto scheduleDto, Long id) {
        User user = userCheckService.getUserByToken(accessToken);
        Schedule original = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleHandler(ErrorStatus._SCHEDULE_NOT_FOUND));// ② 기존 일정 조회

        checkValidUser(original, user);
        scheduleRepository.delete(original);
    }
}
