package com.TaskManagement.TaskFlow.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TaskManagement.TaskFlow.Model.Users;


public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Long id);
    Users findUserByEmail(String email);
}
