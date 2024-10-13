package com.sbukak.domain.board.dto;

import org.springframework.data.domain.Page;

public record GetBoardsResponseDto(
    Page<BoardDto> boards
) {
}
