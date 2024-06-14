package com.TaskManagement.TaskFlow.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.TaskStates;
import com.TaskManagement.TaskFlow.Model.Users;

public interface TaskStateRepository extends  JpaRepository<TaskStates, Long> {

    List<TaskStates> findByBoardAndUser(Boards board, Users user);

    void deleteByBoardId(Long boardId);
}
