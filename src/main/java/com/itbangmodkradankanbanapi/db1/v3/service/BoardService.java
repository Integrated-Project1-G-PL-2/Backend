package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.dto.BoardDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.StatusDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.entities.*;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardOfUserRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.localUserRepository;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardOfUserRepository boardOfUserRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private localUserRepository localUserRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ModelMapper mapper;


    public List<Task> getAllTask(List<String> filterStatuses, String sortBy, String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null) {
            return taskService.findAllTask(filterStatuses, sortBy, boardId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public List<Status> getAllStatus(String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            return statusService.findAllStatus(board);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public Task getTaskById(String boardId, String token, int taskId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null) {
            return taskService.findTaskById(boardId, taskId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public Status getStatusById(String boardId, String token, int statusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            return statusService.findStatusById(board, statusId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public BoardDTO createNewBoard(BoardDTO boardDTO, String token) {
        LocalUser localUser = getLocalUserFromToken(token);
        try {
            Board board = boardRepository.save(new Board(boardDTO.getName()));
            if (localUser != null && board != null) {
                BoardOfUser boardOfUser = new BoardOfUser(localUser, board);
                boardOfUserRepository.save(boardOfUser);
            }
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(localUser);
            return newBoard;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public BoardDTO getBoardById(String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        LocalUser localUser = getLocalUserFromToken(token);
        if (boardOfUser != null) {
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(localUser);
            return newBoard;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public List<BoardOfUser> getAllBoard(String token) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        return boardOfUserRepository.findAllByLocalUser(localUser);

    }

    public TaskDTO addNewTaskToBoard(TaskDTOForAdd task, String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            return taskService.createNewTask(task, board);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public StatusDTO addNewStatusToBoard(StatusDTO statusDTO, String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            return statusService.createNewStatus(statusDTO, board);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public TaskDTO editTaskOfBoard(TaskDTO task, String token, String boardId, int taskId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null) {
            return taskService.updateTask(boardId, taskId, task);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public StatusDTO editStatusOfBoard(StatusDTO statusDTO, String token, String boardId, int statusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            return statusService.updateStatus(board, statusId, statusDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public TaskDTO deleteTaskOfBoard(String token, String boardId, int taskId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null) {
            return taskService.deleteTask(boardId, taskId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public void deleteStatusOfBoard(String token, String boardId, int statusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            statusService.deleteStatus(board, statusId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    public void deleteThenTranferStatusOfBoard(String token, String boardId, int statusId, int newStatusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null) {
            statusService.deleteStatusAndTransfer(board, statusId, newStatusId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the board");
        }
    }

    private BoardOfUser validateUserAndBoard(String token, String boardId) {
        LocalUser localUser = getLocalUserFromToken(token);
        Board board = getBoardById(boardId);
        return boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
    }

    private LocalUser getLocalUserFromToken(String token) {
        String userOid = getUserFromToken(token).getOid();
        return localUserRepository.findById(userOid).orElseThrow(() -> new ItemNotFoundException("User not found"));
    }

    private Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
    }

    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }


}
