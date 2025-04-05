package com.humanicare.backend.repository;

import com.humanicare.backend.domain.BasicSchedule;
import com.humanicare.backend.domain.oauth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BasicScheduleRepository extends JpaRepository<BasicSchedule, Long> {
    List<BasicSchedule> findByUser(User user);

    //LazyInitialization을 피하기 위해 join으로 먼저 가져오는 방법
    @Query("SELECT s FROM BasicSchedule s LEFT JOIN FETCH s.days WHERE s.startTime = :startTime")
    List<BasicSchedule> findWithDaysByStartTime(@Param("startTime") LocalTime startTime);

}
