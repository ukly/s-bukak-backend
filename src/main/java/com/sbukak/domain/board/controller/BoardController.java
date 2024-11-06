package com.sbukak.domain.board.controller;

import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    @Operation(summary = "게시판 목록 조회", description = "게시판 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시판 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetBoardsResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<GetBoardsResponseDto> getBoards(
            @Parameter(description = "게시판 목록 조회 요청 데이터", required = true)
            @ModelAttribute GetBoardsRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                boardService.getBoards(requestDto)
        );
    }

    @GetMapping("/board/{boardId}")
    @Operation(summary = "게시판 상세 조회", description = "특정 게시판의 상세 정보를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시판 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetBoardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<GetBoardResponseDto> getBoard(
            @Parameter(description = "조회할 게시판 ID", required = true, example = "1")
            @PathVariable("boardId") Long boardId
    ) {
        return ResponseEntity.ok(
                boardService.getBoard(boardId)
        );
    }

    @PostMapping("/board")
    @Operation(summary = "게시물 작성", description = "새 게시물을 작성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<Void> createBoard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시물 작성 요청 데이터", required = true,
                    content = @Content(schema = @Schema(implementation = CreateBoardRequestDto.class)))
            @RequestBody CreateBoardRequestDto requestDto
    ) {
        boardService.createBoard(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/board/{boardId}/comment")
    @Operation(summary = "댓글 작성", description = "특정 게시판에 댓글을 작성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<Void> createComment(
            @Parameter(description = "댓글 작성 요청 데이터", required = true)
            @RequestBody CreateCommentRequestDto requestDto
    ) {
        boardService.createComment(requestDto);
        return ResponseEntity.ok().build();
    }
}