package com.humanicare.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.humanicare.backend.converter.BasicScheduleConverter;
import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.dto.BasicScheduleDto;
import com.humanicare.backend.repository.BasicScheduleRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@Getter
@Slf4j
public class SendSchedule {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BasicScheduleRepository basicScheduleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    public SendSchedule(@Qualifier("scheduleRedisTemplate") RedisTemplate<String, Object> redisTemplate, BasicScheduleRepository basicScheduleRepository) {
        this.redisTemplate = redisTemplate;
        this.basicScheduleRepository = basicScheduleRepository;
    }

    // 1분마다 현재 시간과 일치하는 스케줄 조회 후 Redis 전송
    @Scheduled(fixedRate = 60000) // 60초마다 실행
    public void sendCurrentScheduleToRedis() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0); // 초, 나노초 제외

        List<BasicSchedule> schedules = basicScheduleRepository.findWithDaysByStartTime(now);

        
        if (!schedules.isEmpty()) {
            for (BasicSchedule schedule : schedules) {
                BasicScheduleDto.ScheduleDto dto = BasicScheduleConverter.toBasicScheduleDto(schedule);
                if(dto.getUrl() == null)
                    continue;

                // Redis Key-Value 저장
                String redisKey = "schedule:" + schedule.getId();
                redisTemplate.opsForValue().set(redisKey, dto.getUrl());

                try {
                    // JSON 직렬화
                    String json = objectMapper.writeValueAsString(dto);

                    // Pub/Sub 채널에 JSON 메시지 발행
                    redisTemplate.convertAndSend("spring-scheduler-channel", json);

                    log.info("Redis에 저장됨 → Key: {}, 값: {}", redisKey, dto);
                    log.info("JSON 메시지 발행됨 → 채널: spring-scheduler-channel, 내용: {}", json);

                } catch (JsonProcessingException e) {
                    log.error("JSON 직렬화 실패: {}", e.getMessage());
                }
            }
        } else {
            log.info("현재 시간({})에 해당하는 스케줄 없음", now);
        }
    }
}
