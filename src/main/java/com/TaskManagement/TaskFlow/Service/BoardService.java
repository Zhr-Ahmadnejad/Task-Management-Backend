package com.TaskManagement.TaskFlow.Service;


import org.springframework.http.ResponseEntity;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Model.Boards;



public interface BoardService {
    ResponseEntity<?> createBoard(String token ,BoardDto boardDTO);
    Boards findBoardsById(Long id);
    ResponseEntity<?> getUserBoards(String token);
    ResponseEntity<?> updateBoardName(Long boardId, BoardDto boardDTO, String token);
    ResponseEntity<?> deleteBoard(String token, Long boardId);
    ResponseEntity<?> getBoardInfo(String token, Long boardId);
}