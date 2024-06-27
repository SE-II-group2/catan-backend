package com.group2.catanbackend.dto.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class InvalidMoveResponse extends MessageDto{

    public InvalidMoveResponse(String message) {
        this.message = message;
    }

    @Getter
    String message;

}
