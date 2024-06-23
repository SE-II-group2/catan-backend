package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuildVillageMoveDto extends GameMoveDto {
    public BuildVillageMoveDto(int intersectionID) {
        this.intersectionID = intersectionID;
        this.setEventType(GameMoveType.BUILDVILLAGEMOVE);
    }
    private int intersectionID;
}

