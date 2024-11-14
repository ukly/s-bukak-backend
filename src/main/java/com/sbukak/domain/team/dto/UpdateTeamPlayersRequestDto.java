package com.sbukak.domain.team.dto;

import com.sbukak.domain.team.domain.Player;

import java.util.List;

public record UpdateTeamPlayersRequestDto(
    List<Player> players
) {
}
