package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll, MoveRobberDto moveRobberDto) {
        this.diceRoll = diceRoll;
        this.moveRobberDto = moveRobberDto;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    public RollDiceDto(){
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    private int diceRoll;

    private MoveRobberDto moveRobberDto;

}

