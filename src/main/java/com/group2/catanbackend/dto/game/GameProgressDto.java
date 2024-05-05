package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GameProgressDto extends MessageDto{
    public GameProgressDto(GameMoveDto moveDto, PlayerDto playerDto) {
        this.moveDto = moveDto;
        this.playerDto = playerDto;
        setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }
    @Getter
    private GameMoveDto moveDto;
    @Getter
    private PlayerDto playerDto;

}
