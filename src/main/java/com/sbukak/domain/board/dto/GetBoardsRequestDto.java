package com.sbukak.domain.board.dto;

import com.sbukak.domain.board.enums.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetBoardsRequestDto {
    private BoardType boardType = BoardType.FREE;
    private String query = "";

    public GetBoardsRequestDto(
        BoardType boardType,
        String query
    ) {
        this.boardType = boardType;
        this.query = query;
    }
}