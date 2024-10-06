package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.dto.*;
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
import java.util.Optional;

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
    private CollabService collabService;

    @Autowired
    private UserLocalService userLocalService;

    @Autowired
    private ModelMapper mapper;


    public List<Task> getAllTask(List<String> filterStatuses, String sortBy, String token, String boardId) {
        return taskService.findAllTask(filterStatuses, sortBy, boardId);
    }

    public List<BoardOfUser> getAllCollabOfBoard(String boardId) {
        Board board = getBoardById(boardId);
        return collabService.getAllCollab(board);
    }

    public List<Status> getAllStatus(String token, String boardId) {
        Board board = getBoardById(boardId);
        return statusService.findAllStatus(board);
    }

    public Task getTaskById(String boardId, String token, int taskId) {
        return taskService.findTaskById(boardId, taskId);
    }

    public BoardOfUser getCollabOfBoard(String boardId, String collabId) {
        Board board = getBoardById(boardId);
        LocalUser user = getLocalById(collabId).orElseThrow(() -> new ItemNotFoundException("Collab oid '" + collabId + "' not found"));
        return collabService.getCollabById(board, user);
    }


    public Status getStatusById(String boardId, String token, int statusId) {
        Board board = getBoardById(boardId);
        return statusService.findStatusById(board, statusId);
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


    public BoardOfUser addNewCollab(String token, CollabDTO collabDTO, String boardId) {
        User userFromEmail = getUserByEmail(collabDTO.getEmail());
        LocalUser localUserFromEmail = getLocalById(userFromEmail.getOid()).orElse(null);
        if (localUserFromEmail == null && userFromEmail != null) {
            localUserFromEmail = userLocalService.addLocalUser(userFromEmail);
        }
        LocalUser localUserFromToken = getLocalUserFromToken(token);
        if (localUserFromEmail.getOid().equals(localUserFromToken.getOid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not add yourself as collaborator");
        }
        Board board = getBoardById(boardId);
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUserFromEmail, board);
        if (boardOfUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The user is already the collaborator of this board");
        }
        return collabService.addNewCollab(board, localUserFromEmail, collabDTO.getAccess_right());
    }

    public BoardDTO editBoard(BoardDTO boardDTO, String token, String boardId) {
        Board board = getBoardById(boardId);
        board.setVisibility(Board.Visibility.valueOf(boardDTO.getVisibility()));
        board = boardRepository.save(board);
        return mapper.map(board, BoardDTO.class);

    }

    public BoardDTO getBoardById(String token, String boardId) {
        Board board = getBoardById(boardId);
        if (isPublicAccessibility(board)) {
            BoardDTO newBoard = mapper.map(board, BoardDTO.class);
            newBoard.setOwner(getBoardOfUser(boardId).getLocalUser());
            return newBoard;
        }

        BoardDTO newBoard = mapper.map(board, BoardDTO.class);
        newBoard.setOwner(getBoardOfUser(boardId).getLocalUser());
        return newBoard;

    }

    public List<BoardOfUser> getAllBoard(String token) {
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found"));
        return boardOfUserRepository.findAllByLocalUserAndRole(localUser, BoardOfUser.Role.OWNER);
    }

    public TaskDTO addNewTaskToBoard(TaskDTOForAdd task, String token, String boardId) {
        Board board = getBoardById(boardId);
        return taskService.createNewTask(task, board);
    }

    public StatusDTO addNewStatusToBoard(StatusDTO statusDTO, String token, String boardId) {
        Board board = getBoardById(boardId);
        return statusService.createNewStatus(statusDTO, board);
    }

    public TaskDTO editTaskOfBoard(TaskDTOForAdd task, String token, String boardId, int taskId) {
        Board board = getBoardById(boardId);
        return taskService.updateTask(board, taskId, task);
    }

    public StatusDTO editStatusOfBoard(StatusDTO statusDTO, String token, String boardId, int statusId) {
        Board board = getBoardById(boardId);
        return statusService.updateStatus(board, statusId, statusDTO);
    }

    public TaskDTO deleteTaskOfBoard(String token, String boardId, int taskId) {
        return taskService.deleteTask(boardId, taskId);
    }

    public void deleteStatusOfBoard(String token, String boardId, int statusId) {
        Board board = getBoardById(boardId);
        statusService.deleteStatus(board, statusId);
    }

    public void deleteThenTranferStatusOfBoard(String token, String boardId, int statusId, int newStatusId) {
        Board board = getBoardById(boardId);
        statusService.deleteStatusAndTransfer(board, statusId, newStatusId);
    }

    public BoardOfUser validateUserAndBoard(String token, String boardId) {
        LocalUser localUser = getLocalUserFromToken(token);
        Board board = getBoardById(boardId);
        return boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
    }

    private BoardOfUser getBoardOfUser(String boardId) {
        Board board = getBoardById(boardId);
        return boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.OWNER).get(0);
    }

    private LocalUser getLocalUserFromToken(String token) {
        User user = getUserFromToken(token);
        String userOid = user.getOid();
        return localUserRepository.findById(userOid).orElseThrow(() -> new ItemNotFoundException("User not found"));
    }

    public Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board id '" + boardId + "' not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new ItemNotFoundException("Collab email '" + email + "' not found"));
    }

    public LocalUser getLocalUserByEmail(String email) {
        return localUserRepository.findByEmail(email).orElseThrow(() -> new ItemNotFoundException("Collab email '" + email + "' not found"));
    }

    public Optional<LocalUser> getLocalById(String oid) {
        return localUserRepository.findById(oid);
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

    public boolean canAccess(BoardOfUser boardOfUser) {
        return boardOfUser.getRole().toString().equals("OWNER") || boardOfUser.getRole().toString().equals("COLLABORATOR") || boardOfUser.getRole().toString().equals("VISITOR");
    }

    public boolean isOwner(BoardOfUser boardOfUser) {
        return boardOfUser.getRole().toString().equals("OWNER");
    }


}
