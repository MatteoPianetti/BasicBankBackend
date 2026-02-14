package com.example.demo.exception;

public class NoEntityFoundException extends RuntimeException{
    public NoEntityFoundException(String message) {
        super(message);
    }
}
