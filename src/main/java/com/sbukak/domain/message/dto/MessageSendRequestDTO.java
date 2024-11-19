package com.sbukak.domain.message.dto;


public record MessageSendRequestDTO(
        String content,
        Long teamId,
        boolean isAnonymous,
        String userName
) {
}
