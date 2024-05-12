package com.TaskManagement.TaskFlow.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users createUser(Users user) {
        return userRepository.save(user);
    }
}
