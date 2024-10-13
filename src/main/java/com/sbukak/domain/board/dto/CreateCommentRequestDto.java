package com.sbukak.domain.board.dto;

public record CreateCommentRequestDto(
    String content,
    Boolean isAnonymous
) {
}
