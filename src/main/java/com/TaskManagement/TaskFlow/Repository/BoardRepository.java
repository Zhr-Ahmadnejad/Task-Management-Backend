package com.TaskManagement.TaskFlow.Repository;

import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Boards, Long> {
    boolean existsByBoardNameAndUser(String boardName, Users user);
    List<Boards> findByUserId(Long userId);
}
