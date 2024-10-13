package com.sbukak.domain.board.dto;

public record CommentDto(
    String username,
    String userProfileImageUrl,
    String content,
    String createAt
) {
}
