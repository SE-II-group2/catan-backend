package com.group2.catanbackend.exception;

public class GameException extends RuntimeException {
    public GameException(){}
    public GameException(String message){
        super(message);
    }
}
