package com.sbukak.domain.bet.enums;

import java.time.LocalDateTime;

public enum BetType {
    미참여,
    참여완료,
    예측진행중,
    진행예정;

    public static BetType getBetType(LocalDateTime startAt, boolean isParticipated) {
        BetTimeType betTimeType = BetTimeType.getBetTimeType(startAt);
        if (isParticipated) {
            return BetType.참여완료;
        } else {
            return switch (betTimeType) {
                case 예측종료 -> BetType.미참여;
                case 예측진행중 -> BetType.예측진행중;
                case 예측예정 -> BetType.진행예정;
            };
        }
    }
}
