package com.group2.catanbackend.exception;

public class NotActivePlayerException extends GameException{
    public NotActivePlayerException(){}
    public NotActivePlayerException(String message) {
        super(message);
    }
}
