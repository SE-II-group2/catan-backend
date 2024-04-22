package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BuildVillageMoveDto extends GameMoveDto{
    @Getter
    private int row;
    @Getter
    private int col;

}

