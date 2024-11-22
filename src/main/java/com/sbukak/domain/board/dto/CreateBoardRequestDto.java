package com.sbukak.domain.board.dto;

import com.sbukak.domain.board.enums.BoardType;

public record CreateBoardRequestDto(
    String title,
    String content,
    BoardType boardType
) {
}
