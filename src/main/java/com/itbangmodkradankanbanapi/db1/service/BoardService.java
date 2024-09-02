package com.itbangmodkradankanbanapi.db1.service;

import com.itbangmodkradankanbanapi.db1.dto.BoardDTO;
import com.itbangmodkradankanbanapi.db1.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.entities.Board;
import com.itbangmodkradankanbanapi.db1.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import com.itbangmodkradankanbanapi.db1.repositories.BoardOfUserRepository;
import com.itbangmodkradankanbanapi.db1.repositories.BoardRepository;
import com.itbangmodkradankanbanapi.db1.repositories.localUserRepository;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import io.viascom.nanoid.NanoId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
        if (boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board) != null) {
            return taskService.findAllTask(filterStatuses, sortBy, boardId);
        }
        return null;
    }

    public Task getTaskById(String boardId, String token, int taskId) {
        System.out.println("service board1");
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        System.out.println(localUser);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
        System.out.println(board);
        if (boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board) != null) {
            System.out.println("service board2");
            return taskService.findTaskById(taskId);
        }
        return null;
    }

    public BoardDTO createNewBoard(BoardDTO boardDTO, String token) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid())
                .orElseThrow(() -> new ItemNotFoundException("User not found"));
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
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
        if (boardOfUser != null) {
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(localUser);
            return newBoard;
        }
        return null;
    }

    public List<BoardOfUser> getAllBoard(String token) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        List<BoardOfUser> boardOfUsers = boardOfUserRepository.findAllByLocalUser(localUser);
        return boardOfUsers;
    }

    public TaskDTO addNewTaskToBoard(TaskDTOForAdd task, String token, String boardId) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
        if (boardOfUser != null) {
            return taskService.createNewTask(task, board);
        }
        return null;
    }

    public TaskDTO editTaskOfBoard(TaskDTO task, String token, String boardId, int taskId) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
        if (boardOfUser != null) {
            return taskService.updateTask(taskId, task);
        }
        return null;
    }

    public TaskDTO deleteTaskOfBoard(String token, String boardId, int taskId) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
        if (boardOfUser != null) {
            return taskService.deleteTask(taskId);
        }
        return null;
    }

    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }

}
