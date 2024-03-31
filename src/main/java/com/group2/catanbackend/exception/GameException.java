package com.group2.catanbackend.exception;

import com.group2.catanbackend.model.GameDescriptor;

public class GameException extends RuntimeException {
    public GameException(){}
    public GameException(String message){
        super(message);
    }
}
