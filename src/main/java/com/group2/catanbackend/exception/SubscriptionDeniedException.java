package com.group2.catanbackend.exception;

public class SubscriptionDeniedException extends RuntimeException{
    public SubscriptionDeniedException(){}
    public SubscriptionDeniedException(String message){
        super(message);
    }
}
