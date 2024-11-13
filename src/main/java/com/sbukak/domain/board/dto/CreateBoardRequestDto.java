package com.sbukak.domain.board.dto;

import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.global.enums.SportType;

public record CreateBoardRequestDto(
    String title,
    String content,
    BoardType boardType,
    SportType sportType
) {
}
