package com.group2.catanbackend.exception;

public class InvalidGameMoveException extends GameException{
    public InvalidGameMoveException(){}
    public InvalidGameMoveException(String message) {
        super(message);
    }
}
