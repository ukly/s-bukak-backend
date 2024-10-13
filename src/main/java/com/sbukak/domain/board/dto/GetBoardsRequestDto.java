package com.sbukak.domain.board.dto;

import com.sbukak.domain.board.enums.BoardType;
import com.sbukak.global.enums.SportType;

public record GetBoardsRequestDto(
    SportType sportType,
    BoardType boardType,
    String query,
    int page,
    int size,
    boolean myBoardsOnly
) {
    public GetBoardsRequestDto(
        SportType sportType,
        BoardType boardType,
        String query,
        Integer page,
        Integer size,
        Boolean myBoardsOnly
    ) {
        this(
            sportType != null ? sportType : SportType.SOCCER,
            boardType != null ? boardType : BoardType.FREE,
            query,
            page != null ? page : 1,
            size != null ? size : 10,
            myBoardsOnly != null ? myBoardsOnly : false
        );
    }

}