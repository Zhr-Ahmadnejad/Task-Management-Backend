package com.TaskManagement.TaskFlow.Service;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.BoardRepository;
import com.TaskManagement.TaskFlow.Repository.UserRepository;
import com.TaskManagement.TaskFlow.Vo.BoardVo;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.naming.NameNotFoundException;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, TokenService tokenService, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public List<Boards> getAllBoards() {
        return boardRepository.findAll();
    }

    public Optional<Boards> getBoardById(Long boardId) {
        return boardRepository.findById(boardId);
    }

    public ResponseEntity<BoardVo> createBoard(String token, BoardDto boardDTO) throws NameNotFoundException {
        try {
            String[] parts = token.split("\\s+");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String extractedToken = parts[1];

                if (tokenService.validateToken(extractedToken)) {
                    String userEmail = tokenService.extractEmailFromToken(extractedToken);
                    Users user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new NameNotFoundException("User not found"));
                    Boards board = new Boards();
                    board.setBoardName(boardDTO.getBoardName());
                    board.setUser(user);
                    Boards createdBoard = boardRepository.save(board);
                    BoardVo boardVO = new BoardVo(createdBoard.getId(), createdBoard.getBoardName(), user.getId(), createdBoard.getId());
                    return ResponseEntity.ok(boardVO);
                } else {
                    throw new ExceptionAdapter(extractedToken, null);
                }
            } else {
                throw new IllegalArgumentException("Invalid token format");
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while create board", e);
        }
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
