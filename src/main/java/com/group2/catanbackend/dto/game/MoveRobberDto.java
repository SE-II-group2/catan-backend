package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoveRobberDto extends GameMoveDto{

    public MoveRobberDto(int hexagonID, boolean legal) {
        this.hexagonID=hexagonID;
        this.legal=legal;
    }


    private int hexagonID;
    private boolean legal;
}
