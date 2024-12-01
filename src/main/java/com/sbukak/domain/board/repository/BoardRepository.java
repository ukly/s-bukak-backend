package com.sbukak.domain.board.repository;

import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.enums.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.boardType = :boardType and b.user.id = :userId " +
        "and (b.title like CONCAT('%',:query,'%') or b.content like CONCAT('%',:query,'%'))")
    List<Board> findAllByBoardTypeAndUserIdAndTitleOrContentContaining(
        @Param("query") String query,
        @Param("boardType") BoardType boardType,
        Long userId
    );

    @Query("select b from Board b where b.boardType = :boardType " +
        "and (b.title like CONCAT('%',:query,'%') or b.content like CONCAT('%',:query,'%'))")
    List<Board> findAllByBoardTypeAndTitleOrContentContaining(
        @Param("query") String query,
        @Param("boardType") BoardType boardType
    );

    @Query("select b from Board b where " +
        "(b.title like CONCAT('%',:query,'%') or b.content like CONCAT('%',:query,'%'))")
    List<Board> findAllByTitleOrContentContaining(
        @Param("query") String query
    );


    List<Board> findAllByBoardTypeAndUserId(
        BoardType boardType, Long userId
    );

    List<Board> findAllByBoardType(BoardType boardType);

    void deleteByUserId(Long userId);

    @Query("SELECT b.id FROM Board b WHERE b.user.id = :userId")
    List<Long> findIdsByUserId(@Param("userId") Long userId);
}
