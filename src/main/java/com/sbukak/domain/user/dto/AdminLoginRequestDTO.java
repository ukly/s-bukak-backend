package com.sbukak.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequestDTO(
        @NotBlank
        String email,

        @NotBlank
        String password
) {
}
