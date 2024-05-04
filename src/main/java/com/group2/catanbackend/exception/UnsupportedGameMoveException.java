package com.group2.catanbackend.exception;

public class UnsupportedGameMoveException extends GameMoveException{
    public UnsupportedGameMoveException(){}
    public UnsupportedGameMoveException(String message) {super(message);}
}
