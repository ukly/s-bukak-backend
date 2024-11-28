package com.sbukak.domain.message.repository;

import com.sbukak.domain.message.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTeamId(Long teamId);
    @Query("SELECT m FROM Message m WHERE (:teamId = 0 AND m.team IS NULL) OR (m.team.id = :teamId)")
    List<Message> findByTeamIdOrNull(@Param("teamId") Long teamId);

    @Query("SELECT m FROM Message m JOIN FETCH m.user u LEFT JOIN FETCH m.team t WHERE m.id = :messageId")
    Optional<Message> findByIdWithUserAndTeam(@Param("messageId") Long messageId);
}
