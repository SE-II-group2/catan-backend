package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;


public class BuildVillageMoveDto extends GameMoveDto{
    public BuildVillageMoveDto(int row, int col) {
        this.row = row;
        this.col = col;
        this.setEventType(MessageType.GAME_MOVE);
    }

    @Getter
    private final int row;
    @Getter
    private final int col;

}

