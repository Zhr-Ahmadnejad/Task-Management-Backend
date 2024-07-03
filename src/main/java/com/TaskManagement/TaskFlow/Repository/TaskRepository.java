package com.TaskManagement.TaskFlow.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.TaskStates;
import com.TaskManagement.TaskFlow.Model.Tasks;
import com.TaskManagement.TaskFlow.Model.Users;

public interface TaskRepository extends JpaRepository<Tasks, Long> {
    List<Tasks> findByBoardAndUserAndState(Boards board, Users user, TaskStates state);
    Optional<Tasks> findById(Long id);
    List<Tasks> findByState(TaskStates state);
}

