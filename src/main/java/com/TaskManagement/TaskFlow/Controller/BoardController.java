package com.TaskManagement.TaskFlow.Controller;

import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<List<Boards>> getAllBoards() {
        List<Boards> boards = boardService.getAllBoards();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Boards> getBoardById(@PathVariable Long boardId) {
        Optional<Boards> board = boardService.getBoardById(boardId);
        return board.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Boards> createBoard(@RequestBody Boards board) {
        Boards createdBoard = boardService.createBoard(board);
        return new ResponseEntity<>(createdBoard, HttpStatus.CREATED);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<Boards> updateBoard(@PathVariable Long boardId, @RequestBody Boards board) {
        Boards updatedBoard = boardService.updateBoard(boardId, board);
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
