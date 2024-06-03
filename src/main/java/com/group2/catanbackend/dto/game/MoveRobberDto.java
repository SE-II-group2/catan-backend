package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoveRobberDto extends GameMoveDto{

    public MoveRobberDto(int hexagonID) {
        this.setEventType(GameMoveType.MOVEROBBERMOVE);
        this.hexagonID=hexagonID;
    }

    private int hexagonID;
}
