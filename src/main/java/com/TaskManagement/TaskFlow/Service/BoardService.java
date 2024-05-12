package com.TaskManagement.TaskFlow.Service;

import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Boards> getAllBoards() {
        return boardRepository.findAll();
    }

    public Optional<Boards> getBoardById(Long boardId) {
        return boardRepository.findById(boardId);
    }

    public Boards createBoard(Boards board) {
        return boardRepository.save(board);
    }

    public Boards updateBoard(Long boardId, Boards newBoard) {
        return boardRepository.findById(boardId).map(board -> {
            board.setUser(newBoard.getUser());
            board.setStates(newBoard.getStates());
            return boardRepository.save(board);
        }).orElseThrow(() -> new RuntimeException("Board not found"));
    }

    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }
}
