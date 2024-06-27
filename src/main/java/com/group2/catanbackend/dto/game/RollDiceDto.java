package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RollDiceDto extends GameMoveDto{

    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
    }
    private int diceRoll;
}

