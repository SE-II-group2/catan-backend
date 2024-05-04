package com.group2.catanbackend.dto.game;

import lombok.Getter;

@Getter
public class BuildRoadMoveDto extends GameMoveDto{
    public BuildRoadMoveDto(int connection) {
        this.connection = connection;
        this.setEventType(MessageType.GAME_MOVE);
    }

    private final int connection;
}

