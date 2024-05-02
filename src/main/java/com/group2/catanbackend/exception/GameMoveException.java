package com.group2.catanbackend.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GameMoveException extends GameException{
    public GameMoveException(String message){
        super(message);
    }
}
