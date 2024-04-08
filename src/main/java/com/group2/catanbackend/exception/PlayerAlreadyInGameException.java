package com.group2.catanbackend.exception;

public class PlayerAlreadyInGameException extends GameException{
    public PlayerAlreadyInGameException(){}
    public PlayerAlreadyInGameException(String message){
        super(message);
    }
}
