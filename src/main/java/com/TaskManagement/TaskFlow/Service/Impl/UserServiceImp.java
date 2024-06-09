package com.TaskManagement.TaskFlow.Service.Impl;

import java.util.List;
import java.util.Optional;

import javax.naming.NameNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.UserRepository;
import com.TaskManagement.TaskFlow.Service.SecurityService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;

@Service
public class UserServiceImp implements UserService{

    
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Autowired
    public UserServiceImp(UserRepository userRepository, SecurityService securityService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    public Users updateUser(String token, Users newUser) throws NameNotFoundException {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = findUserByEmail(userEmail);
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            return userRepository.save(user);
        } catch (Exception e) {
            // مدیریت استثناء و یا ارسال آن به بالا
            throw new RuntimeException("An error occurred while updating user", e);
        }
    }

    // public ResponseEntity<UserResponse> getUserInfo(HttpServletRequest request) {
    // String token = extractTokenFromRequest(request);
    // if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
    // String email = tokenService.extractEmailFromToken(token);
    // Optional<Users> optionalUser = userRepository.findByEmail(email);
    // if (optionalUser.isPresent()) {
    // Users user = optionalUser.get();
    // UserResponse userResponse = new UserResponse(user.getEmail(),
    // user.getFirstName(), user.getLastName(), user.getPassword());
    // return ResponseEntity.ok(userResponse);
    // } else {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new
    // UserResponse("User not found"));
    // }
    // } else {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new
    // UserResponse("Unauthorized"));
    // }
    // }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public ResponseEntity<?> deleteUser(String token, Long userId){
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users tokenUser = findUserByEmail(userEmail);
            Users user = userRepository.findById(userId).orElse(null);
            if (user != null && tokenUser.equals(user)) {
                userRepository.delete(user);
                return ResponseEntity.status(HttpStatus.OK)
                .body("user deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to delete this user or the user was not found");
            }
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while delete user", e);
            }
        }
    }

    public Long findIdByEmail(String email) {
        Users user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getId();
    }
    

    public Users findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }
}