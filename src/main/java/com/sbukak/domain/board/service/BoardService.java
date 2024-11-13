package com.sbukak.domain.board.service;

import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.domain.Comment;
import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.board.repository.BoardRepository;
import com.sbukak.domain.board.repository.CommentRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.global.enums.SportType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String query = requestDto.getQuery();
        SportType sportType = requestDto.getSportType();
        BoardType boardType = requestDto.getBoardType();
        Long userId = 1L;    //TODO
        boolean hasQuery = query != null && !query.isBlank();
        boolean isOnlyMyBoards = requestDto.isMyBoardsOnly();

        Pageable pageable = PageRequest.of(requestDto.getPage(), requestDto.getSize());
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
            .sportType(requestDto.sportType())
            .user(user)
            .build();
        boardRepository.save(build);
    }

    @Transactional
    public void createComment(CreateCommentRequestDto requestDto, Long boardId) {
        Long userId = 1L;    //TODO
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("board not found"));
        commentRepository.save(
            Comment.builder()
                .content(requestDto.content())
                .isAnonymous(requestDto.isAnonymous())
                .user(user)
                .board(board)
                .build()
        );
    }

}