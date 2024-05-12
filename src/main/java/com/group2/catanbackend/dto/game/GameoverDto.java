package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class GameoverDto extends MessageDto{
    @Getter
    @Setter
    private IngamePlayerDto winner;

    public GameoverDto(IngamePlayerDto winner) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.winner=winner;
    }
}
