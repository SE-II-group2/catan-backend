package com.group2.catanbackend.dto.game;

import lombok.Getter;


@Getter
public class BuildVillageMoveDto extends GameMoveDto{
    public BuildVillageMoveDto(int intersection) {
        this.intersection = intersection;
        this.setEventType(MessageType.GAME_MOVE);
    }

    private final int intersection;

}

