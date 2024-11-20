package com.sbukak.domain.board.dto;

import java.util.List;

public record BoardDto(
    Long id,
    String title,
    String content,
    String username,
    String userProfileImageUrl,
    String createAt,
    List<CommentDto> comments
) {
}
