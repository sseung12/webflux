package com.ss.webflux;

public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
