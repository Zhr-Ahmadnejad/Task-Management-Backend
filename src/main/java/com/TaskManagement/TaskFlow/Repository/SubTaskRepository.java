package com.TaskManagement.TaskFlow.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TaskManagement.TaskFlow.Model.SubTasks;
import com.TaskManagement.TaskFlow.Model.Tasks;

import java.util.List;


public interface SubTaskRepository extends JpaRepository<SubTasks, Long>{
    List<SubTasks> findByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);
}
