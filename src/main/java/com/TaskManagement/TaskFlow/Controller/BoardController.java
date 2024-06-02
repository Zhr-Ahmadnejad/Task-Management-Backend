package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Vo.BoardVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/boards")
public class BoardController {

    private final BoardService boardService;
    

    @Autowired
    public BoardController(BoardService boardService ) {
        this.boardService = boardService;
        
    }

    @PostMapping()
    public ResponseEntity<BoardVo> createBoard(@RequestBody BoardDto boardDTO, @RequestHeader("Authorization") String token) {
        try{
        // Extract user ID from token and pass it to service
        ResponseEntity<BoardVo> response = boardService.createBoard(token ,boardDTO);
        return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
