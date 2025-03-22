package com.humanicare.backend.converter;

import com.humanicare.backend.domain.Diary;
import com.humanicare.backend.domain.oauth.User;
import com.humanicare.backend.dto.DiaryDto;

public class DiaryConverter {

    public static Diary toDiary(final User user, final DiaryDto.DiaryFullDto dto) {
        return Diary.builder()
                .id(dto.getId())
                .diaryFull(dto.getDiaryFull())
                .diarySummary(dto.getDiarySummary())
                .date(dto.getDate())
                .emotion(dto.getEmotion())
                .user(user)
                .build();
    }

    public static DiaryDto.DiaryFullDto toDiaryDto(final Diary diary) {
        return DiaryDto.DiaryFullDto.builder()
                .id(diary.getId())
                .diaryFull(diary.getDiaryFull())
                .diarySummary(diary.getDiarySummary())
                .date(diary.getDate())
                .emotion(diary.getEmotion())
                .build();
    }
}
