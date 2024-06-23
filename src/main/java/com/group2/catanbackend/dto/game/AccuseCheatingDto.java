package com.group2.catanbackend.dto.game;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccuseCheatingDto extends GameMoveDto{
    public AccuseCheatingDto() {
        this.setEventType(GameMoveType.ACCUSECHEATINGMOVE);
    }

    public AccuseCheatingDto(IngamePlayerDto sender) {
        this.sender = sender;
        this.setEventType(GameMoveType.ACCUSECHEATINGMOVE);
    }

    private IngamePlayerDto sender;
}
