package com.TaskManagement.TaskFlow.Service;

import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Vo.BoardVo;


public interface BoardService {
    ResponseEntity<BoardVo> createBoard(String token ,BoardDto boardDTO);
    Boards findBoardsById(Long id);
}