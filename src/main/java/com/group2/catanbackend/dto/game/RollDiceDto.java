package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;


public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
        this.setEventType(MessageType.GAME_MOVE);
    }

    @Getter
    private final int diceRoll;
}

