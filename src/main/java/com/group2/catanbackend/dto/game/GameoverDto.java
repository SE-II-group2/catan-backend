package com.group2.catanbackend.dto.game;

import lombok.Getter;

public class GameoverDto extends GameMoveDto{
    @Getter
    private final PlayerDto winner;

    public GameoverDto(PlayerDto winner) {
        this.winner=winner;
    }
}
