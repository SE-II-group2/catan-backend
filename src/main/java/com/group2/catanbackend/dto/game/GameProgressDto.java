package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GameProgressDto extends MessageDto{
    public GameProgressDto() {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }
    public GameProgressDto(GameMoveDto moveDto, PlayerDto playerDto) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.moveDto = moveDto;
        this.playerDto = playerDto;

    }
    private GameMoveDto moveDto;
    private PlayerDto playerDto;

}
