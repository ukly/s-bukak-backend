package com.sbukak.domain.banner.controller;

import com.sbukak.domain.banner.dto.CreateBannerRequestDto;
import com.sbukak.domain.banner.dto.GetBannersResponseDto;
import com.sbukak.domain.banner.service.BannerService;
import com.sbukak.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/banner")
public class BannerController {
    private final BannerService bannerService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "배너 목록 조회")
    public ResponseEntity<GetBannersResponseDto> getBanners(
        @RequestParam(value = "isDisplayOnly", defaultValue = "false") boolean isDisplayOnly
    ) {
        return ResponseEntity.ok(
            bannerService.getBanners(isDisplayOnly)
        );
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "배너 등록")
    public ResponseEntity<Void> createBanner(
        HttpServletRequest httpServletRequest,
        @RequestPart CreateBannerRequestDto requestDto,
        @RequestPart MultipartFile file
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        bannerService.createBanner(requestDto, token, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bannerId}")
    @Operation(summary = "배너 삭제")
    public ResponseEntity<Void> deleteBanner(
        HttpServletRequest httpServletRequest,
        @PathVariable Long bannerId
    ) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        bannerService.deleteBanner(bannerId, token);
        return ResponseEntity.ok().build();
    }
}
