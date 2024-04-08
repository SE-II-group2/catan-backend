package com.group2.catanbackend.exception;

public class GameFullException extends GameException{
    public GameFullException(){}
    public GameFullException(String message){
        super(message);
    }
}
