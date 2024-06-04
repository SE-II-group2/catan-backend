package com.group2.catanbackend.exception;


public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(){}
    public InvalidConfigurationException(String message){
        super(message);
    }
}
