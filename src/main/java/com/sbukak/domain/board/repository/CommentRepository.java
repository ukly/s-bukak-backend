package com.sbukak.domain.board.repository;

import com.sbukak.domain.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByUserId(Long userId);
    void deleteByBoardIdIn(List<Long> boardIds);
    void deleteByBoardId(Long boardId);
}
