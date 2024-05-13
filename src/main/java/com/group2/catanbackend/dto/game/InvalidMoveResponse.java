package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidMoveResponse extends MessageDto{
    public InvalidMoveResponse(String message) {
        this.message = message;
        this.setEventType(MessageType.INVALID_GAME_MOVE);
    }

    @Getter
    String message;

}
