package com.group2.catanbackend.exception;

public class NoSuchTokenException extends GameException{
    public NoSuchTokenException() {
    }

    public NoSuchTokenException(String message) {
        super(message);
    }
}
