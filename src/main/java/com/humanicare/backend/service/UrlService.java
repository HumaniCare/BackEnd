package com.humanicare.backend.service;

import com.humanicare.backend.dto.BasicScheduleDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final RestTemplate restTemplate;

    public Map<String, String> sendSchedulesToFastAPI(String voiceId, String alias, List<BasicScheduleDto.ScheduleDto> scheduleDtos) {
        String url = "http://fastapi:8000/api/fastapi/schedules"; // 예: http://localhost:8000/schedules

        // 요청 본문 구성
        List<Long> scheduleIdList = new ArrayList<>();
        List<String> scheduleTextList = new ArrayList<>();
        for(BasicScheduleDto.ScheduleDto schedule : scheduleDtos) {
            scheduleIdList.add(schedule.getId());
            scheduleTextList.add(schedule.getScheduleTitle());
        }

        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("voice_id", voiceId);
//        requestBody.put("alias", alias);
        requestBody.put("voice_id", 1);
        requestBody.put("alias", "mom");
        requestBody.put("schedule_id", scheduleIdList);
        requestBody.put("schedule_text", scheduleTextList);

        // 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);

        // POST 요청 전송
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        // 응답 반환
        return response.getBody();
    }
}
