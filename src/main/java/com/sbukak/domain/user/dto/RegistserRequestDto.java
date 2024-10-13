package com.sbukak.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public record RegistserRequestDto(
        @NotBlank
        String sport,

        @NotBlank
        String college,

        @NotBlank
        String team
) {

}
