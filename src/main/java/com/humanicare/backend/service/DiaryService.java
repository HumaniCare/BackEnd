package com.humanicare.backend.service;

import com.humanicare.backend.domain.Diary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DiaryService {
    public Diary getDiary(LocalDate date) {
        return Diary.builder().build();
    }
}
