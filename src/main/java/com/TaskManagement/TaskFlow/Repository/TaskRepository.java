package com.TaskManagement.TaskFlow.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TaskManagement.TaskFlow.Model.Tasks;

public interface TaskRepository extends JpaRepository<Tasks, Long> {
 
}
