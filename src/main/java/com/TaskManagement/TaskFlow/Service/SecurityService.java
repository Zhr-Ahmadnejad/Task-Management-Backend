package com.TaskManagement.TaskFlow.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.SecurityRepository;


@Service
public class SecurityService {

    @Autowired
    private SecurityRepository securityRepository;

    public void registerUser(Users user) throws Exception {

        if (securityRepository.existsByEmail(user.getEmail())) {
            throw new Exception("این ایمیل قبلاً استفاده شده است");
        }

        securityRepository.save(user);
    }

}
