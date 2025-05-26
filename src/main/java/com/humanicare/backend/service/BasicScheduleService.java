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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicScheduleService {

    private final BasicScheduleRepository basicScheduleRepository;
    private final UserCheckService userCheckService;
    private final ApplicationContext applicationContext;
    private final UrlService urlService;


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

    @Transactional
    public void createSchedule(String accessToken, List<BasicScheduleDto.ScheduleDto> scheduleDtos) {
        User user = userCheckService.getUserByToken(accessToken);

        List<BasicScheduleDto.ScheduleDto> scheduleDtoList = new ArrayList<>();

        //기존 스케줄을 Map으로 구성
        List<BasicSchedule> originSchedules = basicScheduleRepository.findByUser(user);
        Map<String, BasicSchedule> originScheduleMap = originSchedules.stream()
                .collect(Collectors.toMap(BasicSchedule::getScheduleTitle, s -> s));

        Set<String> incomingTitles = new HashSet<>();

        // 요청된 스케줄 처리 (create or update)
        for (BasicScheduleDto.ScheduleDto dto : scheduleDtos) {
            incomingTitles.add(dto.getScheduleTitle());

            if (originScheduleMap.containsKey(dto.getScheduleTitle())) {
                // update
                BasicScheduleService proxy = applicationContext.getBean(BasicScheduleService.class);
                proxy.updateSchedule(originScheduleMap.get(dto.getScheduleTitle()), dto);
            } else {
                // create
                BasicSchedule newSchedule = BasicScheduleConverter.toBasicSchedule(user, dto);
                BasicSchedule save = basicScheduleRepository.save(newSchedule);
                BasicScheduleDto.ScheduleDto savedDto = BasicScheduleConverter.toBasicScheduleDto(save);
                scheduleDtoList.add(savedDto);
            }
        }

        // 요청에서 빠진 기존 스케줄 삭제
        for (BasicSchedule old : originSchedules) {
            if (!incomingTitles.contains(old.getScheduleTitle())) {
                basicScheduleRepository.delete(old);
            }
        }

        log.info("scheduleDtoList: {}", scheduleDtoList);
        Map<String, String> urls = urlService.sendSchedulesToFastAPI(user.getVoiceUrl(), user.getAlias(), scheduleDtoList);
        for(Map.Entry<String, String> entry : urls.entrySet()) {
            basicScheduleRepository.findById(Long.parseLong(entry.getKey())).ifPresent(schedule -> {
                log.info("schedule: {}, url: {}", schedule.getScheduleTitle(), entry.getValue());
                schedule.updateUrl(entry.getValue());
            });
        }
    }

    @Transactional
    public void updateSchedule(BasicSchedule schedule, BasicScheduleDto.ScheduleDto scheduleDto) {
        schedule.changeSchedule(scheduleDto.getScheduleTitle(), scheduleDto.getStartTime(), scheduleDto.getDays());
    }
}
