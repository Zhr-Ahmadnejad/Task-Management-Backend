package com.TaskManagement.TaskFlow.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TaskManagement.TaskFlow.Model.SubTasks;

public interface SubTaskssRepository extends JpaRepository<SubTasks, Long>{
    
}
