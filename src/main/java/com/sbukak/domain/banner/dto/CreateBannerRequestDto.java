package com.sbukak.domain.banner.dto;

import java.time.LocalDate;

public record CreateBannerRequestDto(
    String title,
    String league,
    LocalDate startDate,
    LocalDate endDate
) {
}
