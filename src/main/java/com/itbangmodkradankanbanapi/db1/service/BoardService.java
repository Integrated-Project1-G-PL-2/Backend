package com.itbangmodkradankanbanapi.db1.service;

import com.itbangmodkradankanbanapi.db1.entities.Board;
import com.itbangmodkradankanbanapi.db1.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import com.itbangmodkradankanbanapi.db1.repositories.BoardOfUserRepository;
import com.itbangmodkradankanbanapi.db1.repositories.BoardRepository;
import com.itbangmodkradankanbanapi.db1.repositories.localUserRepository;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Task> getAllTask(List<String> filterStatuses , String sortBy, String token, String boardId){
        LocalUser localUser = localUserRepository.findById(getUserFromToken(token).getOid()).orElseThrow(() -> new ItemNotFoundException("User not found" ));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found" ));
        if(boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser,board) != null){
            return taskService.findAllTask(filterStatuses,sortBy,boardId);
        }
        return null;
    }

    private User getUserFromToken(String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }

}
