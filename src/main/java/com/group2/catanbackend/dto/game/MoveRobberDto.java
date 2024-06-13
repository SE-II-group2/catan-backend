package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MoveRobberDto extends GameMoveDto{

    public MoveRobberDto(int hexagonID, boolean legal) {
        this.setEventType(GameMoveType.MOVEROBBERMOVE);
        this.hexagonID=hexagonID;
        this.legal=legal;
    }

    public MoveRobberDto(){
        this.setEventType(GameMoveType.MOVEROBBERMOVE);
    }

    private int hexagonID;
    private boolean legal;
}
