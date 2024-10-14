package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardOfUserRepository;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
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
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
        if (boardOfUser == null) {
            throw new ItemNotFoundException("User don't have permission to this board");
        }
        return boardOfUser;
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

    public void deleteCollabById(BoardOfUser boardOfUser) {
        boardOfUserRepository.delete(boardOfUser);
    }
}
