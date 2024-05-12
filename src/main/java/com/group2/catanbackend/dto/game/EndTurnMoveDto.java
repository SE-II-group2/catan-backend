package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EndTurnMoveDto extends GameMoveDto{
    private ArrayList<PlayerDto> turnOder;
    public EndTurnMoveDto() {
        this.setEventType(GameMoveType.ENTTURNMOVE);
    }

    public EndTurnMoveDto(ArrayList<PlayerDto> turnOder) {
        this.setEventType(GameMoveType.ENTTURNMOVE);
        this.turnOder=turnOder;
    }
}
