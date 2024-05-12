package com.TaskManagement.TaskFlow.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TaskManagement.TaskFlow.Model.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
}
