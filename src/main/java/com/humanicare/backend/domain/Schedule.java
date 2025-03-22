package com.humanicare.backend.domain;

import com.humanicare.backend.domain.oauth.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedule")
public class Schedule extends BaseEntity{
    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_title", unique = true)
    private String scheduleTitle;

    @Column(name = "start_time", unique = true)
    private LocalTime startTime;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 추천
    @JoinColumn(name = "user_id")
    private User user;
}