package com.humanicare.backend.repository;

import com.humanicare.backend.domain.Schedule;
import com.humanicare.backend.domain.oauth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
}
