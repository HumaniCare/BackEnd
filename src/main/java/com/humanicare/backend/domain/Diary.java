package com.humanicare.backend.domain;

import com.humanicare.backend.domain.oauth.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary")
public class Diary {
    @Id
    @Column(name = "diary_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_full", unique = true)
    private String diaryFull;

    @Column(name = "diary_summary", unique = true)
    private String diarySummary;

    @Column(name = "date", unique = true)
    private LocalDate date;

    @Column
    private Emotion emotion;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 추천
    @JoinColumn(name = "user_id")
    private User user;
}
