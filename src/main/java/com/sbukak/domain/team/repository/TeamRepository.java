package com.sbukak.domain.team.repository;

import com.sbukak.domain.team.domain.College;
import com.sbukak.domain.team.domain.Team;
import com.sbukak.global.enums.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllBySportType(SportType sportType);

    Optional<Team> findByName(String name);

    Optional<Team> findByNameAndCollege(String name, College college);
}
