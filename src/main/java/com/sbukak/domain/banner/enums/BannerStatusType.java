package com.sbukak.domain.banner.enums;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum BannerStatusType {
    DISPLAY("노출"),
    PENDING("대기"),
    END("종료");

    final String desc;

    BannerStatusType(String desc) {
        this.desc = desc;
    }

    public static BannerStatusType getBannerStatus(LocalDate startAt, LocalDate endAt) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startAt)) {
            return BannerStatusType.PENDING;
        }
        if (now.isAfter(endAt)) {
            return BannerStatusType.END;
        }
        return BannerStatusType.DISPLAY;
    }
}
