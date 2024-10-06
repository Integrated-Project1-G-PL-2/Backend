package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardOfUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollabService {

    @Autowired
    private BoardOfUserRepository boardOfUserRepository;

    public List<BoardOfUser> getAllCollab(Board board) {
        List<BoardOfUser> visitors = boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.VISITOR);
        List<BoardOfUser> collaborators = boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.COLLABORATOR);

        List<BoardOfUser> result = new ArrayList<>();
        result.addAll(visitors);
        result.addAll(collaborators);

        return result;
    }

    public BoardOfUser getCollabById(Board board, LocalUser localUser) {
        return boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
    }

    public BoardOfUser addNewCollab(Board board, LocalUser localUser, String accessRight) {
        BoardOfUser.Role role;
        if (accessRight.equals("WRITE")) {
            role = BoardOfUser.Role.COLLABORATOR;
        } else {
            role = BoardOfUser.Role.VISITOR;
        }
        BoardOfUser boardOfUser = new BoardOfUser(localUser, board, role);
        return boardOfUserRepository.save(boardOfUser);
    }

}
