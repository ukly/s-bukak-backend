package com.sbukak.domain.board.controller;

import com.sbukak.domain.board.dto.*;
import com.sbukak.domain.board.service.BoardService;
import com.sbukak.global.jwt.JwtTokenProvider;
import com.sbukak.global.oauth2.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;

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
            HttpServletRequest httpServletRequest,
            @Parameter(description = "게시판 목록 조회 요청 데이터", required = true)
            @ModelAttribute GetBoardsRequestDto requestDto
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        return ResponseEntity.ok(
                boardService.getBoards(requestDto, token)
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
            HttpServletRequest httpServletRequest,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시물 작성 요청 데이터", required = true,
                    content = @Content(schema = @Schema(implementation = CreateBoardRequestDto.class)))
            @RequestBody CreateBoardRequestDto requestDto
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        boardService.createBoard(requestDto, token);
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
            HttpServletRequest httpServletRequest,
            @Parameter(description = "댓글 작성 요청 데이터", required = true)
            @RequestBody CreateCommentRequestDto requestDto,
            @PathVariable("boardId") Long boardId
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        boardService.createComment(requestDto, boardId, token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/board/{boardId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @Operation(summary = "게시물 삭제", description = "게시물 및 관련 댓글 삭제")
    public ResponseEntity<String> deleteBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Parameter(description = "삭제할 게시물 ID", required = true, example = "1")
                                                @PathVariable("boardId") Long boardId) {
        Long userId = userDetails.getUser().getId();
        boardService.deleteBoard(userId, boardId);
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }

    @DeleteMapping("/comment/{commentId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @Operation(summary = "댓글 삭제", description = "댓글 삭제")
    public ResponseEntity<String> deleteCommentById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @Parameter(description = "삭제할 댓글 ID", required = true, example = "1")
                                                    @PathVariable("commentId") Long commentId
    ) {
        Long userId = userDetails.getUser().getId();
        boardService.deleteCommentById(commentId, userId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }
}