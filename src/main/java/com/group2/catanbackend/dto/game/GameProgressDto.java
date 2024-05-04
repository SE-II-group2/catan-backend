package com.group2.catanbackend.dto.game;

import lombok.Getter;

public class GameProgressDto extends MessageDto{
    public GameProgressDto(GameMoveDto moveDto, PlayerDto playerDto) {
        this.moveDto = moveDto;
        this.playerDto = playerDto;
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }

    @Getter
    private final GameMoveDto moveDto;
    @Getter
    private final PlayerDto playerDto;

}
