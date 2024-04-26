package com.group2.catanbackend.dto.game;

import lombok.Getter;

public class InvalidMoveDto extends MessageDto{
    public InvalidMoveDto(String message) {
        this.message = message;
        this.setEventType(MessageType.INVALID_GAME_MOVE);
    }

    @Getter
    String message;

}
