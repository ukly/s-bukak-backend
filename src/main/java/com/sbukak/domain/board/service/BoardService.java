package com.sbukak.domain.board.service;

import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.domain.Comment;
import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.board.repository.BoardRepository;
import com.sbukak.domain.board.repository.CommentRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.enums.SportType;
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
    private final UserService userService;

    @Transactional(readOnly = true)
    public GetBoardsResponseDto getBoards(
        GetBoardsRequestDto requestDto,
        String token
    ) {
        User user = userService.getUserByToken(token);
        List<Board> boards = getBoards(requestDto, user);
        return new GetBoardsResponseDto(
            boards.stream()
                .sorted(Comparator.comparing(Board::getCreateAt).reversed())
                .map(Board::toBoardDto)
                .toList()
        );
    }

    private List<Board> getBoards(GetBoardsRequestDto requestDto, User user) {
        String query = requestDto.getQuery();
        SportType sportType = requestDto.getSportType();
        BoardType boardType = requestDto.getBoardType();
        boolean hasQuery = query != null && !query.isBlank();

        if (boardType == BoardType.MY_POST || boardType == BoardType.MY_COMMENT) {
            List<Board> allBoards;
            if (hasQuery) {
                allBoards = boardRepository.findAllBySportTypeAndTitleOrContentContaining(query, sportType);
            } else {
                allBoards = boardRepository.findAllBySportType(sportType);
            }
            if (boardType == BoardType.MY_POST) {
                return allBoards.stream().filter(board -> board.getUser() == user).toList();
            }
            return allBoards.stream()
                .filter(board -> board.getComments().stream().anyMatch(comment -> comment.getUser() == user))
                .toList();
        }
        if (hasQuery) {
            return boardRepository.findAllBySportTypeAndBoardTypeAndTitleOrContentContaining(
                query, sportType, boardType
            );
        }
        return boardRepository.findAllBySportTypeAndBoardType(
            sportType, boardType
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