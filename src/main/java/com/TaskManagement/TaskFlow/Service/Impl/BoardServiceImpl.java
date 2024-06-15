package com.TaskManagement.TaskFlow.Service.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Dto.BoardDto;
import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;
import com.TaskManagement.TaskFlow.Model.Boards;
import com.TaskManagement.TaskFlow.Model.TaskStates;
import com.TaskManagement.TaskFlow.Model.Users;
import com.TaskManagement.TaskFlow.Repository.BoardRepository;
import com.TaskManagement.TaskFlow.Repository.TaskStateRepository;
import com.TaskManagement.TaskFlow.Service.BoardService;
import com.TaskManagement.TaskFlow.Service.TokenService;
import com.TaskManagement.TaskFlow.Service.UserService;
import com.TaskManagement.TaskFlow.Vo.BoardVo;
import com.TaskManagement.TaskFlow.Vo.TaskStateVo;

@Transactional
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final TaskStateRepository taskStateRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository, UserService userService, TokenService tokenService,
            TaskStateRepository taskStateRepository) {
        this.boardRepository = boardRepository;
        this.userService = userService;
        this.tokenService = tokenService;
        this.taskStateRepository = taskStateRepository;
    }

    @Override
    public ResponseEntity<?> createBoard(String token, BoardDto boardDTO) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            if (boardRepository.existsByBoardNameAndUser(boardDTO.getBoardName(), user)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A board with the same name already exists for this user");
            }

            // Map DTO to Entity
            Boards board = new Boards();
            board.setBoardName(boardDTO.getBoardName());
            board.setUser(user);
            // Save to database
            Boards savedBoard = boardRepository.save(board);
            List<TaskStates> saveTaskStates = new ArrayList<>();
            for (String taskStateName : boardDTO.getTaskStates()) {
                TaskStates taskState = new TaskStates();
                taskState.setStateName(taskStateName);
                taskState.setBoard(savedBoard);
                taskState.setUser(user);
                TaskStates savedTaskState = taskStateRepository.save(taskState);
                saveTaskStates.add(savedTaskState);
            }
            // Map Entity to VO and return
            BoardVo boardVo = mapEntityToVO(savedBoard, saveTaskStates);
            return new ResponseEntity<>(boardVo, HttpStatus.CREATED);

        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while updating user", e);
            }
        }
    }

    @Override
    public ResponseEntity<?> updateBoardName(Long boardId, BoardDto boardDTO, String token) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = findBoardsById(boardId);
            if (board.getUser() != user) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("The board with this ID is not found for this user.");
            } else if (boardRepository.existsByBoardNameAndUser(boardDTO.getBoardName(), user)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A board with the same name already exists for this user");
            } else {
                if(boardDTO.getBoardName() != null){
                    board.setBoardName(boardDTO.getBoardName());
                }
                Boards savedBoard = boardRepository.save(board);
                List<TaskStates> saveTaskStates = new ArrayList<>();
                if(boardDTO.getTaskStates() != null){
                    taskStateRepository.deleteByBoardId(boardId);
                    for (String taskStateName : boardDTO.getTaskStates()) {
                        TaskStates taskState = new TaskStates();
                        taskState.setStateName(taskStateName);
                        taskState.setBoard(savedBoard);
                        taskState.setUser(user);
                        taskStateRepository.save(taskState);
                    }
                }
                for(TaskStates getTaskStates : taskStateRepository.findByBoardAndUser(savedBoard, user)){
                    saveTaskStates.add(getTaskStates);
                }
                // Map Entity to VO and return
                BoardVo boardVo = mapEntityToVO(savedBoard, saveTaskStates);
                return new ResponseEntity<>(boardVo, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while updating board", e);
            }
        }
    }

    @Override
    public ResponseEntity<?> getUserBoards(String token) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            List<Boards> boards = boardRepository.findByUserId(user.getId());
            List<BoardVo> boardVo = new ArrayList<>();
            for (Boards board : boards) {
                List<TaskStates> taskStates = taskStateRepository.findByBoardAndUser(board,user);
                boardVo.add(mapEntityToVO(board,taskStates));
            }
            return new ResponseEntity<>(boardVo, HttpStatus.CREATED);
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while updating user", e);
            }
        }
    }

    
    @Override
    public ResponseEntity<?> getBoardInfo(String token, Long boardId) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = boardRepository.findById(boardId).orElse(null);
            List<TaskStates> taskStates = taskStateRepository.findByBoardAndUser(board, user);
            BoardVo boardVo = mapEntityToVO(board, taskStates);
            return new ResponseEntity<>(boardVo, HttpStatus.OK);
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while get board info", e);
            }
        }
    }

    
    @Override
    public ResponseEntity<?> deleteBoard(String token, Long boardId) {
        try {
            String extractedToken = tokenService.validateToken(token);
            String userEmail = tokenService.extractEmailFromToken(extractedToken);
            Users user = userService.findUserByEmail(userEmail);
            Boards board = boardRepository.findById(boardId).orElse(null);
            if (board != null && board.getUser().equals(user)) {
                boardRepository.delete(board);
                return ResponseEntity.status(HttpStatus.OK)
                .body("Board deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to delete this board or the board was not found");
            }
        } catch (Exception e) {
            if (e instanceof ExpiredTokenException || e instanceof InvalidTokenException
                    || e instanceof TokenValidationException) {
                // Handle token related exceptions
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            } else {
                // Handle other exceptions
                throw new RuntimeException("An error occurred while delete board", e);
            }
        }
    }

    private BoardVo mapEntityToVO(Boards board, List<TaskStates> taskStates) {
        BoardVo boardVO = new BoardVo();
        boardVO.setId(board.getId());
        boardVO.setBoardName(board.getBoardName());
        List<TaskStateVo> taskStateVOs = new ArrayList<>();
        for (TaskStates taskState : taskStates) {
            TaskStateVo taskStateVO = new TaskStateVo();
            taskStateVO.setId(taskState.getId());
            taskStateVO.setStateName(taskState.getStateName());
            taskStateVO.setBoardId(taskState.getBoard().getId());
            taskStateVOs.add(taskStateVO);
        }
        boardVO.setTaskStates(taskStateVOs);
        return boardVO;
    }

    private BoardVo mapEntityToVO(Boards board) {
        BoardVo boardVO = new BoardVo();
        boardVO.setId(board.getId());
        boardVO.setBoardName(board.getBoardName());
        return boardVO;
    }

    public Boards findBoardsById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
