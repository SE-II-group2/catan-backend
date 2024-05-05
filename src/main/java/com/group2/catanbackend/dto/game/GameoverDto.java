package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class GameoverDto extends MessageDto{
    @Getter
    @Setter
    private PlayerDto winner;

    public GameoverDto(PlayerDto winner) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.winner=winner;
    }
}
