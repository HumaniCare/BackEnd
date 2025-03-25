package com.humanicare.backend.repository;

import com.humanicare.backend.domain.BasicSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicScheduleRepository extends JpaRepository<BasicSchedule, Long> {
}
