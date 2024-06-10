package com.group2.catanbackend.dto.game;


public class AccuseCheatingDto extends GameMoveDto{
    public AccuseCheatingDto() {
        this.setEventType(GameMoveType.ACCUSECHEATINGMOVE);
    }
}
