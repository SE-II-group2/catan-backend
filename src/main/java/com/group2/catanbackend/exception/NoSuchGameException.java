package com.group2.catanbackend.exception;

public class NoSuchGameException extends GameException{
    public NoSuchGameException(){}
    public NoSuchGameException(String message){
        super(message);
    }
}
