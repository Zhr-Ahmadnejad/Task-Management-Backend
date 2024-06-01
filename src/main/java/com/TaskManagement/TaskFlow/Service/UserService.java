package com.TaskManagement.TaskFlow.Service;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.UserRepository;
import com.TaskManagement.TaskFlow.Response.UserResponse;

import javax.naming.NameNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Autowired
    public UserService(UserRepository userRepository, SecurityService securityService, TokenService tokenService) {
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
            String[] parts = token.split("\\s+");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String extractedToken = parts[1];
                
                if (tokenService.validateToken(extractedToken)) {
                    String email = tokenService.extractEmailFromToken(extractedToken);
                    Users existingUser = userRepository.findByEmail(email)
                            .orElseThrow(() -> new NameNotFoundException("User not found"));
                    existingUser.setFirstName(newUser.getFirstName());
                    existingUser.setLastName(newUser.getLastName());
                    existingUser.setEmail(newUser.getEmail());
                    existingUser.setPassword(newUser.getPassword());
                    return userRepository.save(existingUser);
                } else {
                    throw new ExceptionAdapter(extractedToken, null);
                }
            } else {
                throw new Exception("Invalid token format");
            }
        } catch (Exception e) {
            // مدیریت استثناء و یا ارسال آن به بالا
            throw new RuntimeException("An error occurred while updating user", e);
        }
    }
    

    public ResponseEntity<UserResponse> getUserInfo(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
            String email = tokenService.extractEmailFromToken(token);
            Optional<Users> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                UserResponse userResponse = new UserResponse(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserResponse("User not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserResponse("Unauthorized"));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public Long findIdByEmail(String email){
        return userRepository.findIdByEmail(email);
    }
}
