package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BuildRoadMoveDto extends GameMoveDto{
    @Getter
    private int fromIntersection;
    @Getter
    private int toIntersection;
}

