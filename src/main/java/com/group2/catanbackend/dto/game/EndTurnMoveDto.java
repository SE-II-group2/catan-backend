package com.group2.catanbackend.dto.game;

import lombok.NoArgsConstructor;


public class EndTurnMoveDto extends GameMoveDto{

    public EndTurnMoveDto() {
        this.setEventType(GameMoveType.ENTTURNMOVE);
    }
}
