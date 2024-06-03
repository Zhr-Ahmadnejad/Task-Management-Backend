package com.TaskManagement.TaskFlow.Service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.SecurityRepository;
import com.TaskManagement.TaskFlow.Service.SecurityService;
import com.TaskManagement.TaskFlow.Service.TokenService;


@Service
public class SecurityServiceImpl implements SecurityService{
    
    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<?> loginUser(String email, String password) {
        try {
            // جستجوی کاربر بر اساس ایمیل
            Optional<Users> optionalUser = securityRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                // چک کردن رمزعبور
                if (user.getPassword().equals(password)) {
                    // ایجاد توکن برای کاربر موفق
                    String token = tokenService.generateToken(email);
                    return ResponseEntity.ok().body(token);
                } else {
                    // رمزعبور اشتباه است
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("اطلاعات کاربری اشتباه است");
                }
            } else {
                // کاربری با این ایمیل یافت نشد
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("اطلاعات کاربری اشتباه است");
            }
        } catch (Exception e) {
            // خطایی در هنگام ورود رخ داده است
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    

    public ResponseEntity<?> registerUser(Users user) {
        try {
            // بررسی اینکه آیا کاربر با این ایمیل قبلاً ثبت‌نام کرده یا نه
            if (securityRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("این ایمیل قبلاً استفاده شده است");
            }
            // ذخیره کاربر در پایگاه داده
            securityRepository.save(user);
            String token = tokenService.generateToken(user.getEmail());
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            // خطایی در هنگام ثبت‌نام رخ داده است
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
