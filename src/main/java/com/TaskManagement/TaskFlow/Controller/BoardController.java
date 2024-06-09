package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Service.BoardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;

    }

    @PostMapping()
    public ResponseEntity<?> createBoard(@RequestBody BoardDto boardDTO, @RequestHeader("Authorization") String token) {
        try {
            // Extract user ID from token and pass it to service
            ResponseEntity<?> response = boardService.createBoard(token, boardDTO);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping
    public ResponseEntity<?> getUserBoards(@RequestHeader("Authorization") String token) {
        try {
            ResponseEntity<?> response = boardService.getUserBoards(token);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<?> updateBoardName(@RequestHeader("Authorization") String token, @PathVariable Long boardId,
            @RequestBody BoardDto boardDTO) {
        try {
            ResponseEntity<?> response = boardService.updateBoardName(boardId, boardDTO, token);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@RequestHeader("Authorization") String token, @PathVariable Long boardId) {
        return boardService.deleteBoard(token, boardId);

    }
}
