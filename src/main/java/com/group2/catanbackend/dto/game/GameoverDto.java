package com.group2.catanbackend.dto.game;

import lombok.Getter;

public class GameoverDto extends MessageDto{
    @Getter
    private final PlayerDto winner;

    public GameoverDto(PlayerDto winner) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.winner=winner;
    }
}
