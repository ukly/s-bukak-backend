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
    private int page = 1;
    private int size = 10;
    private boolean myBoardsOnly = false;

    public GetBoardsRequestDto(
        SportType sportType,
        BoardType boardType,
        String query,
        Integer page,
        Integer size,
        Boolean myBoardsOnly
    ) {
        this.sportType = sportType != null ? sportType : SportType.SOCCER;
        this.boardType = boardType != null ? boardType : BoardType.FREE;
        this.query = query;
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 10;
        this.myBoardsOnly = myBoardsOnly != null ? myBoardsOnly : false;
    }

}