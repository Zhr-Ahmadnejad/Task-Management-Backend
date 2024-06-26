package com.TaskManagement.TaskFlow.Repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TaskManagement.TaskFlow.Model.Users;

public interface SecurityRepository extends  JpaRepository<Users, Long>  {
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
}
