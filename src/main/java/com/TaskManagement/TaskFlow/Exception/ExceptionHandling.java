package com.TaskManagement.TaskFlow.Exception;

public class ExceptionHandling extends RuntimeException {
    private final String frontendMessage ;

    public ExceptionHandling(String frontendMessage, String message) {
        super(message);
        this.frontendMessage = frontendMessage;
    }

    public String getFrontendMessage() {
        return frontendMessage;
    }
}
