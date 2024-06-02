package com.TaskManagement.TaskFlow.Service.Impl;

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
            String[] parts = token.split("\\s+");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String extractedToken = parts[1];
    
                if (tokenService.validateToken(extractedToken)) {
                    // Extract email from token
                    String userEmail = tokenService.extractEmailFromToken(extractedToken);
                    Users user = userService.findUserByEmail(userEmail);
    
                    // Check if a board with the same name already exists for the user
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
    
                } else {
                    throw new ExceptionAdapter(extractedToken, null);
                }
            } else {
                throw new Exception("Invalid token format");
            }
        } catch (Exception e) {
            // مدیریت استثناء و یا ارسال آن به بالا
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

    public Boards findBoardsById(Long id){
        return boardRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
