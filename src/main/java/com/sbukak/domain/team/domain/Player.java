package com.sbukak.domain.team.domain;

public record Player(
    String id,
    String name,
    String number,
    Position position,
    Boolean isSelected
) {
    public record Position(
       int top,
       int left
    ) {}
}