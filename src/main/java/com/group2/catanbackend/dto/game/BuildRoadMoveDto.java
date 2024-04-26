package com.group2.catanbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class BuildRoadMoveDto extends GameMoveDto{
    public BuildRoadMoveDto(int fromIntersection, int toIntersection) {
        this.fromIntersection = fromIntersection;
        this.toIntersection = toIntersection;
        this.setEventType(MessageType.GAME_MOVE);
    }

    @Getter
    private final int fromIntersection;
    @Getter
    private final int toIntersection;
}

