package com.sbukak.domain.board.dto;

import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.global.enums.SportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetBoardsRequestDto {
    // Getter methods
    private SportType sportType = SportType.SOCCER;
    private BoardType boardType = BoardType.FREE;
    private String query = "";

    public GetBoardsRequestDto(
        SportType sportType,
        BoardType boardType,
        String query
    ) {
        this.sportType = sportType;
        this.boardType = boardType;
        this.query = query;
    }
}