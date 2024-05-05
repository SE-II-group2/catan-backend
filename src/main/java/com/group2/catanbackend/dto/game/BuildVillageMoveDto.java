package com.group2.catanbackend.dto.game;

import lombok.Getter;


@Getter
public class BuildVillageMoveDto extends GameMoveDto{
    public BuildVillageMoveDto(int intersection) {
        this.intersection = intersection;
    }

    private final int intersection;

}

