package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Vo.BoardVo;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> createBoard(@RequestBody BoardDto boardDTO, @RequestHeader("Authorization") String token) {
        try{
        // Extract user ID from token and pass it to service
        ResponseEntity<?> response = boardService.createBoard(token ,boardDTO);
        return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping
    public ResponseEntity<List<BoardVo>> getUserBoards(@RequestHeader("Authorization") String token) {
        try {
            List<BoardVo> response = boardService.getUserBoards(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
