package com.sbukak.domain.board.dto;

import java.util.List;

public record GetBoardsResponseDto(
    List<BoardDto> boards
) {
}
