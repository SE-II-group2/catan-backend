package com.group2.catanbackend.dto.game;

import lombok.Getter;

@Getter
public class BuildRoadMoveDto extends GameMoveDto{
    public BuildRoadMoveDto(int connectionId) {
        this.connectionID = connectionId;
    }

    private final int connectionID;
}

