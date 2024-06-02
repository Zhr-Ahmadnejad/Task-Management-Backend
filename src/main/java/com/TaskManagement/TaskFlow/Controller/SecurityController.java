package com.TaskManagement.TaskFlow.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Service.SecurityService;

@Controller
@RequestMapping("/api")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        return securityService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Users user) {
        return securityService.loginUser(user.getEmail(), user.getPassword());
    }


}
