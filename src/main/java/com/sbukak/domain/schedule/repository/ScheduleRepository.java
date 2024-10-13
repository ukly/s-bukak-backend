package com.sbukak.domain.schedule.repository;

import com.sbukak.global.enums.SportType;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllBySportType(SportType sportType);
    @Query("SELECT s FROM Schedule s WHERE s.homeTeam = :team OR s.awayTeam = :team")
    List<Schedule> findAllByTeam(@Param("team") Team team);
}
