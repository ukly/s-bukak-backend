package com.sbukak.domain.board.controller;

import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    @Operation(summary = "게시판 목록 조회")
    public ResponseEntity<GetBoardsResponseDto> getBoards(
        @ModelAttribute GetBoardsRequestDto requestDto
    ) {
        return ResponseEntity.ok(
            boardService.getBoards(requestDto)
        );
    }

    @GetMapping("/board/{boardId}")
    @Operation(summary = "게시판 상세 조회")
    public ResponseEntity<GetBoardResponseDto> getBoard(
        @PathVariable("boardId") Long boardId
    ) {
        return ResponseEntity.ok(
            boardService.getBoard(boardId)
        );
    }

    @PostMapping("/board")
    @Operation(summary = "게시물 작성")
    public ResponseEntity<Void> createBoard(
        @RequestBody CreateBoardRequestDto requestDto
    ) {
        boardService.createBoard(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/board/{boardId}/comment")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<Void> createComment(
        @RequestBody CreateCommentRequestDto requestDto
    ) {
        boardService.createComment(requestDto);
        return ResponseEntity.ok().build();
    }
}