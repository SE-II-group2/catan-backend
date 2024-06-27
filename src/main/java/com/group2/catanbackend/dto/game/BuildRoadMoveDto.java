package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuildRoadMoveDto extends GameMoveDto{

    public BuildRoadMoveDto(int connectionID) {
        this.connectionID = connectionID;
    }
    private int connectionID;
}

