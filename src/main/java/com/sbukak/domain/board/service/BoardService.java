package com.sbukak.domain.board.service;

import com.sbukak.domain.board.domain.Board;
import com.sbukak.domain.board.domain.Comment;
import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.domain.board.repository.BoardRepository;
import com.sbukak.domain.board.repository.CommentRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        List<Board> boards = getBoardsProcess(requestDto, token);
        return new GetBoardsResponseDto(
            boards.stream()
                .sorted(Comparator.comparing(Board::getCreateAt).reversed())
                .map(Board::toBoardDto)
                .toList()
        );
    }

    private List<Board> getBoardsProcess(GetBoardsRequestDto requestDto, String token) {
        String query = requestDto.getQuery();
        BoardType boardType = requestDto.getBoardType();
        boolean hasQuery = query != null && !query.isBlank();

        if (boardType == BoardType.MY_POST || boardType == BoardType.MY_COMMENT) {
            User user = userService.getUserOrNull(token);
            if (user == null) {
                return new ArrayList<>();
            }
            List<Board> allBoards;
            if (hasQuery) {
                allBoards = boardRepository.findAllByTitleOrContentContaining(query);
            } else {
                allBoards = boardRepository.findAll();
            }
            if (boardType == BoardType.MY_POST) {
                return allBoards.stream().filter(board -> board.getUser() == user).toList();
            }
            return allBoards.stream()
                .filter(board -> board.getComments().stream().anyMatch(comment -> comment.getUser() == user))
                .toList();
        }
        if (hasQuery) {
            return boardRepository.findAllByBoardTypeAndTitleOrContentContaining(
                query, boardType
            );
        }
        return boardRepository.findAllByBoardType(boardType);
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

    @Transactional
    public void deleteBoard(Long userId, Long boardId){
        // 게시물 존재 여부 확인 및 작성자 검증
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        if (!board.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 게시물을 삭제할 권한이 없습니다.");
        }

        // 해당 게시물의 댓글 삭제
        commentRepository.deleteByBoardId(boardId);

        // 게시물 삭제
        boardRepository.delete(board);
    }

    @Transactional
    public void deleteCommentById(Long commentId, Long userId) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 요청자가 댓글 작성자인지 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.delete(comment);
    }
}