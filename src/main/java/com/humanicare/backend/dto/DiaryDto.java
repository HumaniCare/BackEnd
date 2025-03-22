package com.humanicare.backend.dto;

import com.humanicare.backend.domain.Emotion;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

public class DiaryDto {

    //메인 화면에서 감정만 가져오고 싶을 때 사용
    @Getter
    @Builder
    public static class DiaryEmotionDto {
        private Emotion emotion;

        public DiaryEmotionDto() {
        }

        public DiaryEmotionDto(Emotion emotion) {
            this.emotion = emotion;
        }
    }

    //어차피 날짜 선택해서 볼거니까 이것만 있어도 되지 않을까
    @Getter
    @Builder
    public static class DiaryFullDto {
        private Long id;
        private String diaryFull;
        private String diarySummary;
        private LocalDate date;
        private Emotion emotion;

        public DiaryFullDto() {
        }

        public DiaryFullDto(Long id, String diaryFull, String diarySummary, LocalDate date, Emotion emotion) {
            this.id = id;
            this.diaryFull = diaryFull;
            this.diarySummary = diarySummary;
            this.date = date;
            this.emotion = emotion;
        }
    }
}
