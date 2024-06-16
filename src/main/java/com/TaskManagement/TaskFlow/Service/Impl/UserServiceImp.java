package com.TaskManagement.TaskFlow.Service.Impl;

import java.util.List;

import javax.naming.NameNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.TaskManagement.TaskFlow.Dto.UserDto;
import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.UserRepository;
import com.TaskManagement.TaskFlow.Service.SecurityService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.UserVo;

@Transactional
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

    public ResponseEntity<?> getUserById(String token,Long userId) {
        try{
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = findUserByEmail(userEmail);
            UserVo userVo = mapEntitieToVo(user);
            return new ResponseEntity<>(userVo, HttpStatus.OK);
        }catch (Exception e) {
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

    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    public ResponseEntity<?> updateUser(String token, UserDto newUser) throws NameNotFoundException {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = findUserByEmail(userEmail);
            if(newUser.getEmail() != null){
                if(userRepository.findByEmail(newUser.getEmail()).isPresent()){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("for this email accound is find");
                }
                user.setEmail(newUser.getEmail());
            }
            if(newUser.getFirstName() != null){
                user.setFirstName(newUser.getFirstName());
            }
            if(newUser.getLastName() != null){
                user.setLastName(newUser.getLastName());
            }
            if(newUser.getPassword() != null){
                user.setPassword(newUser.getPassword());
            }
            Users saveUser = userRepository.save(user);
            UserVo userVo = mapEntitieToVo(saveUser);
            return new ResponseEntity<>(userVo, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while update user", e);
            }
        }
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

        private UserVo mapEntitieToVo(Users user) {
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setFirstName(user.getFirstName());
        userVo.setLastName(user.getLastName());
        userVo.setEmail(user.getEmail());
        userVo.setPassword(user.getPassword());
        return userVo;

    }
}