package com.sbukak.domain.team.repository;

import com.sbukak.domain.team.domain.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
    Optional<College> findByName(String name);
}
