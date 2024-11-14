package com.sbukak.domain.bet.enums;

import java.time.LocalDateTime;

public enum BetTimeType {
    예측예정("아래 두 경우 제외한 모든 경우"),
    예측진행중("경기일 기준 5일 전 (25일 17시 경기면, 20일 17시 예측 시작)"),
    예측종료("경기 당일 경기 시간 1시간 전 종료");

    private String description;

    BetTimeType(String description) {
        this.description = description;
    }

    public static BetTimeType getBetTimeType(LocalDateTime startAt) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(startAt.minusHours(1))) {
            return 예측종료;
        }
        if (now.isAfter(startAt.minusDays(5))) {
            return 예측진행중;
        }
        return 예측예정;
    }
}
