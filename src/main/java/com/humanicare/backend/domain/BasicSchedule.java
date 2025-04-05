package com.humanicare.backend.domain;

import com.humanicare.backend.domain.oauth.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "basic_schedule")
public class BasicSchedule extends BaseEntity{
    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String scheduleTitle;
    private LocalTime startTime;

    @ElementCollection(targetClass = Day.class)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private List<Day> days = new ArrayList<>(); //기본값 설정할 때 java측 field가 null이 되는걸 방지하기 위해 new로 객체 생성

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 추천
    @JoinColumn(name = "user_id")
    private User user;

    public void changeSchedule(String title, LocalTime time, List<Day> days) {
        this.scheduleTitle = title;
        this.startTime = time;
        this.days = days;
    }
}
