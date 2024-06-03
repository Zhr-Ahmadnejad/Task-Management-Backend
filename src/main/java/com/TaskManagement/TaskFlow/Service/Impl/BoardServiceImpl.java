package com.TaskManagement.TaskFlow.Service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.BoardRepository;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.BoardVo;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository, UserService userService, TokenService tokenService) {
        this.boardRepository = boardRepository;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<BoardVo> createBoard(String token, BoardDto boardDTO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            if (boardRepository.existsByBoardNameAndUser(boardDTO.getBoardName(), user)) {
                throw new Exception("A board with the same name already exists for this user");
            }

            // Map DTO to Entity
            Boards board = new Boards();
            board.setBoardName(boardDTO.getBoardName());
            board.setUser(user);
            // Save to database
            Boards savedBoard = boardRepository.save(board);

            // Map Entity to VO and return
            BoardVo boardVo = mapEntityToVO(savedBoard);
            return new ResponseEntity<>(boardVo, HttpStatus.CREATED);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while updating user", e);
        }
    }

    @Override
    public List<BoardVo> getUserBoards(String token) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Long userId = userService.findIdByEmail(userEmail);
            List<Boards> boards = boardRepository.findByUserId(userId);
            List<BoardVo> boardVos = new ArrayList<>();
            for (Boards board : boards) {
                boardVos.add(mapEntityToVO(board));
            }
            return boardVos;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while updating user", e);
        }
    }

    private BoardVo mapEntityToVO(Boards board) {
        BoardVo boardVO = new BoardVo();
        boardVO.setId(board.getId());
        boardVO.setBoardName(board.getBoardName());
        // Map other fields if needed
        return boardVO;
    }

    public Boards findBoardsById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
