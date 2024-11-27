package com.sbukak.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record EditProfileRequestDTO(
        @NotBlank
        String name
) {
}
