package com.sbukak.domain.banner.service;

import com.sbukak.domain.banner.domain.Banner;
import com.sbukak.domain.banner.dto.CreateBannerRequestDto;
import com.sbukak.domain.banner.dto.GetBannersResponseDto;
import com.sbukak.domain.banner.enums.BannerStatusType;
import com.sbukak.domain.banner.repository.BannerRepository;
import com.sbukak.domain.user.entity.User;
import com.sbukak.domain.user.service.UserService;
import com.sbukak.global.aws.dto.AmazonS3UploadResult;
import com.sbukak.global.aws.service.AmazonS3Service;
import com.sbukak.global.enums.SportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final UserService userService;
    private final AmazonS3Service amazonS3Service;

    @Transactional(readOnly = true)
    public GetBannersResponseDto getBanners(boolean isDisplayOnly) {
        List<Banner> banners = bannerRepository.findAll();

        return new GetBannersResponseDto(
            banners.stream()
                .filter(banner -> !isDisplayOnly || banner.getBannerStatusType() == BannerStatusType.DISPLAY)
                .map(banner -> new GetBannersResponseDto.BannerDto(
                    banner.getId(),
                    banner.getTitle(),
                    banner.getUser().getEmail() != null ? banner.getUser().getEmail() : banner.getUser().getName(),
                    banner.getSportType().getName(),
                    banner.getStartAt(),
                    banner.getEndAt(),
                    banner.getBannerStatusType().getDesc(),
                    banner.getFileName(),
                    banner.getFileUrl()
                ))
                .sorted(Comparator.comparing(GetBannersResponseDto.BannerDto::id, Comparator.reverseOrder())).toList()
        );
    }

    @Transactional
    public void createBanner(CreateBannerRequestDto requestDto, String token, MultipartFile file) {
        User user = userService.checkAdminByToken(token);
        AmazonS3UploadResult uploadResult = amazonS3Service.upload(file, "banner");
        Banner banner = Banner.builder()
            .title(requestDto.title())
            .user(user)
            .sportType(SportType.getByName(requestDto.league()))
            .startAt(requestDto.startDate())
            .endAt(requestDto.endDate())
            .fileName(uploadResult.fileName())
            .fileUrl(uploadResult.fileUrl())
            .build();
        bannerRepository.save(banner);
    }

    @Transactional
    public void deleteBanner(Long bannerId, String token) {
        userService.checkAdminByToken(token);
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new IllegalArgumentException("banner not found"));
        bannerRepository.delete(banner);
    }
}
