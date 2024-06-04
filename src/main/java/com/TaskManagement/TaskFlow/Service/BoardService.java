package com.TaskManagement.TaskFlow.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Vo.BoardVo;


public interface BoardService {
    ResponseEntity<?> createBoard(String token ,BoardDto boardDTO);
    Boards findBoardsById(Long id);
    ResponseEntity<?> getUserBoards(String token);
    ResponseEntity<?> updateBoardName(Long boardId, BoardDto boardDTO, String token);
}