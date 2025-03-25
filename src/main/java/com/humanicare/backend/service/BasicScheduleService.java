package com.humanicare.backend.service;

import com.humanicare.backend.apiPayload.code.status.ErrorStatus;
import com.humanicare.backend.apiPayload.exception.handler.BasicScheduleHandler;
import com.humanicare.backend.converter.BasicScheduleConverter;
import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.repository.BasicScheduleRepository;
import com.humanicare.backend.service.user.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicScheduleService {

    private final BasicScheduleRepository basicScheduleRepository;
    private final UserCheckService userCheckService;

    //BasicSchedule의 User id와 현재 로그인된 User의 id가 일치하지 않으면 Forbidden
    private void checkValidUser(BasicSchedule schedule, User currentUser) {
        if (!schedule.getUser().getId().equals(currentUser.getId())) {
            throw new BasicScheduleHandler(ErrorStatus._FORBIDDEN);
        }
    }

    public List<BasicSchedule> getAllSchedule(String accessToken) {
        User user = userCheckService.getUserByToken(accessToken);
        return basicScheduleRepository.findByUser(user);
    }


    public BasicSchedule getSchedule(final Long id) {
        Optional<BasicSchedule> schedule = basicScheduleRepository.findById(id);
        if(schedule.isPresent()) {
            return schedule.get();
        } else {
            throw new BasicScheduleHandler(ErrorStatus._BASIC_SCHEDULE_NOT_FOUND);
        }
    }

    public void createSchedule(String accessToken, BasicScheduleDto.ScheduleDto scheduleDto) {
        User user = userCheckService.getUserByToken(accessToken);
        basicScheduleRepository.save(BasicScheduleConverter.toBasicSchedule(user, scheduleDto));
    }

    public void updateSchedule(String accessToken, BasicScheduleDto.ScheduleDto scheduleDto, Long id) {
        User user = userCheckService.getUserByToken(accessToken);
        BasicSchedule original = basicScheduleRepository.findById(id)
                .orElseThrow(() -> new BasicScheduleHandler(ErrorStatus._BASIC_SCHEDULE_NOT_FOUND)); // ② 기존 일정 조회

        checkValidUser(original, user);
        basicScheduleRepository.save(BasicScheduleConverter.toBasicSchedule(user, scheduleDto));
    }

    public void deleteSchedule(String accessToken, Long id) {
        User currentUser = userCheckService.getUserByToken(accessToken);
        BasicSchedule schedule = basicScheduleRepository.findById(id)
                .orElseThrow(() -> new BasicScheduleHandler(ErrorStatus._BASIC_SCHEDULE_NOT_FOUND));

        checkValidUser(schedule, currentUser);

        basicScheduleRepository.delete(schedule);
    }
}
