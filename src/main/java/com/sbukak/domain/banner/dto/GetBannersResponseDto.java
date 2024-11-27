package com.sbukak.domain.banner.dto;

import java.time.LocalDate;
import java.util.List;

public record GetBannersResponseDto(
    List<BannerDto> banners
) {
    public record BannerDto(
        Long id,
        String title,
        String managerId,
        String league,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String fileName,
        String fileUrl
    ) {
    }
}