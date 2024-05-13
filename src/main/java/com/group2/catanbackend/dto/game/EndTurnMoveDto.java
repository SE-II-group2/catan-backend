package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EndTurnMoveDto extends GameMoveDto{

    public EndTurnMoveDto() {
        this.setEventType(GameMoveType.ENTTURNMOVE);
    }
    public EndTurnMoveDto(IngamePlayerDto nextPlayer) {
        this.setEventType(GameMoveType.ENTTURNMOVE);
        this.nextPlayer=nextPlayer;
    }

    private IngamePlayerDto nextPlayer;
}
