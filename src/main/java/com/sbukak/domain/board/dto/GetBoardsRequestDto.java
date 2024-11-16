package com.sbukak.domain.board.dto;

import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.global.enums.SportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetBoardsRequestDto {
    // Getter methods
    private SportType sportType = SportType.SOCCER;
    private BoardType boardType = BoardType.FREE;
    private String query;
    private boolean myBoardsOnly = false;

    public GetBoardsRequestDto(
        SportType sportType,
        BoardType boardType,
        String query,
        boolean myBoardsOnly
    ) {
        this.sportType = sportType;
        this.boardType = boardType;
        this.query = query;
        this.myBoardsOnly = myBoardsOnly;
    }
}