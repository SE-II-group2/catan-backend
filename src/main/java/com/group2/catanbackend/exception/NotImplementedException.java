package com.group2.catanbackend.exception;

public class NotImplementedException extends RuntimeException{
    public NotImplementedException(){}
    public NotImplementedException(String message){
        super(message);
    }
}
