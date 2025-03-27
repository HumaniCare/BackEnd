package com.humanicare.backend.repository;

import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.oauth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasicScheduleRepository extends JpaRepository<BasicSchedule, Long> {
    List<BasicSchedule> findByUser(User user);
}
