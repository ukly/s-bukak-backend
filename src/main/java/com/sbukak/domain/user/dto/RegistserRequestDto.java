package com.sbukak.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistserRequestDto(
        @NotBlank
        String email,

        @NotBlank
        String name,

        @NotBlank
        Boolean isTeamLeader,

        String sport,

        String college,

        String team
) {

}
