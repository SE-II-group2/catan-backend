package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class InvalidMoveResponse extends MessageDto{
    public InvalidMoveResponse(){
        super(MessageType.INVALID_GAME_MOVE);
    }
    public InvalidMoveResponse(String message) {
        super(MessageType.INVALID_GAME_MOVE);
        this.message = message;
    }

    @Getter
    String message;

}
