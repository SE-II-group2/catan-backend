package com.group2.catanbackend.exception;

public class NotAuthorizedException extends GameException {
    public NotAuthorizedException(){}
    public NotAuthorizedException(String message){
        super(message);
    }
}
