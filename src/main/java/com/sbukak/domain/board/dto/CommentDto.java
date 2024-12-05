package com.sbukak.domain.board.dto;

public record CommentDto(
        Long commentId,
    String username,
    String userProfileImageUrl,
    String content,
    String createAt
) {
}
