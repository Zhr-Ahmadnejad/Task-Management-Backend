package com.TaskManagement.TaskFlow.Service;

public interface TokenService {

    String validateToken(String token) throws Exception;

    String extractEmailFromToken(String extractedToken);

    String generateToken(String email);

}

