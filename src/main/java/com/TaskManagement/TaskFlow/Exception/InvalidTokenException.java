package com.TaskManagement.TaskFlow.Exception;
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}