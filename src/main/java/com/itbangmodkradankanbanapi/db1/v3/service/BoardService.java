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
import com.itbangmodkradankanbanapi.exception.UnauthorizeAccessException;
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
        Board board = getBoardById(boardId);
        if (isPublicAccessibility(board)) {
            return taskService.findAllTask(filterStatuses, sortBy, boardId);
        }
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null && canAccess(boardOfUser)) {
            return taskService.findAllTask(filterStatuses, sortBy, boardId);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public List<Status> getAllStatus(String token, String boardId) {
        Board board = getBoardById(boardId);
        if (isPublicAccessibility(board)) {
            return statusService.findAllStatus(board);
        }
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null && canAccess(boardOfUser)) {
            return statusService.findAllStatus(board);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public Task getTaskById(String boardId, String token, int taskId) {
        Board board = getBoardById(boardId);
        if (isPublicAccessibility(board)) {
            return taskService.findTaskById(boardId, taskId);
        }
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null && canAccess(boardOfUser)) {
            return taskService.findTaskById(boardId, taskId);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public Status getStatusById(String boardId, String token, int statusId) {
        Board board = getBoardById(boardId);
        if (isPublicAccessibility(board)) {
            return statusService.findStatusById(board, statusId);
        }
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null && canAccess(boardOfUser)) {
            return statusService.findStatusById(board, statusId);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public BoardDTO createNewBoard(BoardDTO boardDTO, String token) {
        LocalUser localUser = getLocalUserFromToken(token);
        try {
            Board board = boardRepository.save(new Board(boardDTO.getName(), "PRIVATE"));
            if (localUser != null && board != null) {
                BoardOfUser boardOfUser = new BoardOfUser(localUser, board, BoardOfUser.Role.OWNER);
                boardOfUserRepository.save(boardOfUser);
            }
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(localUser);
            return newBoard;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public BoardDTO editBoard(BoardDTO boardDTO, String token, String boardId) {
        System.out.println("test");
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && isOwner(boardOfUser)) {
            board.setVisibility(Board.Visibility.valueOf(boardDTO.getVisibility()));
            board = boardRepository.save(board);
            return mapper.map(board, BoardDTO.class);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }

    }

//    public BoardDTO grantPrivilegeToBoard(BoardDTO boardDTO, String token, String boardId) {
//        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
//        Board board = getBoardById(boardId);
//        if (boardOfUser != null && isOwner(boardOfUser)) {
//            board.setVisibility(Board.Visibility.valueOf(boardDTO.getVisibility()));
//            board = boardRepository.save(board);
//            return mapper.map(board, BoardDTO.class);
//        } else {
//            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
//        }
//
//    }

    public BoardDTO getBoardById(String token, String boardId) {
        Board board = getBoardById(boardId);
        if (isPublicAccessibility(board)) {
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(getBoardOfUser(boardId).getLocalUser());
            return newBoard;
        }
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null && canAccess(boardOfUser)) {
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(getBoardOfUser(boardId).getLocalUser());
            return newBoard;
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public List<BoardOfUser> getAllBoard(String token) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        return boardOfUserRepository.findAllByLocalUserAndRole(localUser, BoardOfUser.Role.OWNER);
    }

    public TaskDTO addNewTaskToBoard(TaskDTOForAdd task, String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            return taskService.createNewTask(task, board);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public StatusDTO addNewStatusToBoard(StatusDTO statusDTO, String token, String boardId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            return statusService.createNewStatus(statusDTO, board);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public TaskDTO editTaskOfBoard(TaskDTOForAdd task, String token, String boardId, int taskId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            return taskService.updateTask(board, taskId, task);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public StatusDTO editStatusOfBoard(StatusDTO statusDTO, String token, String boardId, int statusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            return statusService.updateStatus(board, statusId, statusDTO);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public TaskDTO deleteTaskOfBoard(String token, String boardId, int taskId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            return taskService.deleteTask(boardId, taskId);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public void deleteStatusOfBoard(String token, String boardId, int statusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            statusService.deleteStatus(board, statusId);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public void deleteThenTranferStatusOfBoard(String token, String boardId, int statusId, int newStatusId) {
        BoardOfUser boardOfUser = validateUserAndBoard(token, boardId);
        Board board = getBoardById(boardId);
        if (boardOfUser != null && canModify(boardOfUser)) {
            statusService.deleteStatusAndTransfer(board, statusId, newStatusId);
        } else {
            throw new UnauthorizeAccessException(HttpStatus.FORBIDDEN, "User not a board owner");
        }
    }

    public BoardOfUser validateUserAndBoard(String token, String boardId) {
        LocalUser localUser = getLocalUserFromToken(token);
        Board board = getBoardById(boardId);
        return boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
    }

    private BoardOfUser getBoardOfUser(String boardId) {
        Board board = getBoardById(boardId);
        return boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.OWNER);
    }

    private LocalUser getLocalUserFromToken(String token) {
        User user = getUserFromToken(token);
        String userOid = user.getOid();
        return localUserRepository.findById(userOid).orElseThrow(() -> new ItemNotFoundException("User not found"));
    }

    public Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
    }

    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }

    public boolean isPublicAccessibility(Board board) {
        return board.getVisibility().toString().equals("PUBLIC");
    }

    public boolean canModify(BoardOfUser boardOfUser) {
        return boardOfUser.getRole().toString().equals("OWNER") || boardOfUser.getRole().toString().equals("COLLABORATOR");
    }

    private boolean canAccess(BoardOfUser boardOfUser) {
        return boardOfUser.getRole().toString().equals("OWNER") || boardOfUser.getRole().toString().equals("COLLABORATOR") || boardOfUser.getRole().toString().equals("VISITOR");
    }

    private boolean isOwner(BoardOfUser boardOfUser) {
        return boardOfUser.getRole().toString().equals("OWNER");
    }


}
