package com.sbukak.domain.board.service;

import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.domain.Comment;
import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.board.repository.BoardRepository;
import com.sbukak.domain.board.repository.CommentRepository;
import com.sbukak.domain.board.tmp.SportType;
import com.sbukak.domain.board.tmp.User;
import com.sbukak.domain.board.tmp.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetBoardsResponseDto getBoards(
        GetBoardsRequestDto requestDto
    ) {
        String query = requestDto.query();
        SportType sportType = requestDto.sportType();
        BoardType boardType = requestDto.boardType();
        Long userId = 1L;    //TODO
        boolean hasQuery = query != null && !query.isBlank();
        boolean isOnlyMyBoards = requestDto.myBoardsOnly();

        Pageable pageable = PageRequest.of(requestDto.page(), requestDto.size());
        Page<Board> pageBoards;

        if (hasQuery && isOnlyMyBoards) {   // query와 userId가 모두 있는 경우
            pageBoards = boardRepository.findAllBySportTypeAndBoardTypeAndUserIdAndTitleOrContentContaining(
                query, sportType, boardType, userId, pageable
            );
        } else if (hasQuery) {  // query는 있지만 userId는 없는 경우
            pageBoards = boardRepository.findAllBySportTypeAndBoardTypeAndTitleOrContentContaining(
                query, sportType, boardType, pageable
            );
        } else if (isOnlyMyBoards) {    // userId는 있지만 query는 없는 경우
            pageBoards = boardRepository.findAllBySportTypeAndBoardTypeAndUserId(
                sportType, boardType, userId, pageable
            );
        } else {    // query와 userId가 모두 없는 경우
            pageBoards = boardRepository.findAllBySportTypeAndBoardType(
                sportType, boardType, pageable
            );
        }
        return new GetBoardsResponseDto(pageBoards.map(Board::toBoardDto));
    }

    @Transactional(readOnly = true)
    public GetBoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("board not found"));
        return new GetBoardResponseDto(board.toBoardDto());
    }

    @Transactional
    public void createBoard(CreateBoardRequestDto requestDto) {
        Long userId = 1L;    //TODO
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
        Board build = Board.builder()
            .boardType(requestDto.boardType())
            .title(requestDto.title())
            .content(requestDto.content())
            .user(user)
            .build();
        boardRepository.save(build);
    }

    @Transactional
    public void createComment(CreateCommentRequestDto requestDto) {
        Long userId = 1L;    //TODO
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
        commentRepository.save(
            Comment.builder()
                .content(requestDto.content())
                .isAnonymous(requestDto.isAnonymous())
                .user(user)
                .build()
        );
    }

}