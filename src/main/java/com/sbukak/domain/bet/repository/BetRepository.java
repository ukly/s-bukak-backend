package com.sbukak.domain.bet.repository;

import com.sbukak.domain.bet.domain.Bet;
import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.schedule.domain.Schedule;
import com.sbukak.domain.user.entity.User;
import com.sbukak.global.enums.SportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    boolean existsByUserAndSchedule(User user, Schedule schedule);

    List<Bet> findAllByUser(User user);

    void deleteByUserId(Long userId);
}
