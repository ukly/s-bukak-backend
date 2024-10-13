package com.sbukak.global.enums;

import lombok.Getter;

@Getter
public enum SportType {
    SOCCER("축구", "국민대학교 운동장"),
    BASKETBALL("농구", "국민대학교 농구장");
    final String name;
    final String place;
    SportType(String name, String place) {
        this.name = name;
        this.place = place;
    }
}
