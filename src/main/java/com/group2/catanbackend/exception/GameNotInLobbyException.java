package com.group2.catanbackend.exception;

public class GameNotInLobbyException extends GameException{
    public GameNotInLobbyException(){}

    public GameNotInLobbyException(String message){
        super(message);
    }
}
