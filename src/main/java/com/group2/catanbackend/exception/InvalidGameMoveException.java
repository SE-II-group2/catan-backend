package com.group2.catanbackend.exception;

public class InvalidGameMoveException extends GameMoveException{
    public InvalidGameMoveException(){}
    public InvalidGameMoveException(String message) {
        super(message);
    }
}
