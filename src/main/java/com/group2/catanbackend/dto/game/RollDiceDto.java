package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@NoArgsConstructor
@Getter
@Setter
public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }

    public RollDiceDto(int diceRoll, HashMap<String, int[]>playerResources) {
        this.diceRoll = diceRoll;
        this.playerResources=playerResources;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    private int diceRoll;

    private HashMap<String, int[]> playerResources;
}

