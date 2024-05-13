package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameProgressDto extends MessageDto{
    public GameProgressDto() {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }
    public GameProgressDto(GameMoveDto gameMoveDto) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.gameMoveDto=gameMoveDto;

    }
    private GameMoveDto gameMoveDto;

}
