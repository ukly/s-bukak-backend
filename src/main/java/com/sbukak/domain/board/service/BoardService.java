package com.sbukak.domain.board.service;

import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.domain.Comment;
import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.board.repository.BoardRepository;
import com.sbukak.domain.board.repository.CommentRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.repository.UserRepository;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.enums.SportType;
import com.sbukak.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Transactional(readOnly = true)
    public GetBoardsResponseDto getBoards(
        GetBoardsRequestDto requestDto,
        String token
    ) {
        String query = requestDto.getQuery();
        SportType sportType = requestDto.getSportType();
        BoardType boardType = requestDto.getBoardType();
        User user = userService.getUserByToken(token);
        boolean hasQuery = query != null && !query.isBlank();
        boolean isOnlyMyBoards = requestDto.isMyBoardsOnly();
        List<Board> boards;

        if (hasQuery && isOnlyMyBoards) {   // query와 userId가 모두 있는 경우
            boards = boardRepository.findAllBySportTypeAndBoardTypeAndUserIdAndTitleOrContentContaining(
                query, sportType, boardType, user.getId()
            );
        } else if (hasQuery) {  // query는 있지만 userId는 없는 경우
            boards = boardRepository.findAllBySportTypeAndBoardTypeAndTitleOrContentContaining(
                query, sportType, boardType
            );
        } else if (isOnlyMyBoards) {    // userId는 있지만 query는 없는 경우
            boards = boardRepository.findAllBySportTypeAndBoardTypeAndUserId(
                sportType, boardType, user.getId()
            );
        } else {    // query와 userId가 모두 없는 경우
            boards = boardRepository.findAllBySportTypeAndBoardType(
                sportType, boardType
            );
        }
        return new GetBoardsResponseDto(
            boards.stream()
                .sorted(Comparator.comparing(Board::getCreateAt).reversed())
                .map(Board::toBoardDto)
                .toList()
        );
    }

    @Transactional(readOnly = true)
    public GetBoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("board not found"));
        return new GetBoardResponseDto(board.toBoardDto());
    }

    @Transactional
    public void createBoard(CreateBoardRequestDto requestDto, String token) {
        User user = userService.getUserByToken(token);
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
    public void createComment(CreateCommentRequestDto requestDto, Long boardId, String token) {
        User user = userService.getUserByToken(token);
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