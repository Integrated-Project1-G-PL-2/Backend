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
        List<BoardOfUser> visitors = boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.READ);
        List<BoardOfUser> collaborators = boardOfUserRepository.findBoardOfUserByBoardAndRole(board, BoardOfUser.Role.WRITE);

        List<BoardOfUser> result = new ArrayList<>();
        result.addAll(visitors);
        result.addAll(collaborators);

        return result;
    }

    public BoardOfUser getCollabById(Board board, LocalUser localUser) {
        BoardOfUser boardOfUser = boardOfUserRepository.findBoardOfUserByLocalUserAndBoard(localUser, board);
        if (boardOfUser == null || boardOfUser.getRole().toString().equals("OWNER")) {
            throw new ItemNotFoundException("User don't have permission to this board");
        }
        System.out.println(boardOfUser);
        return boardOfUser;
    }

    public BoardOfUser addNewCollab(Board board, LocalUser localUser, String accessRight) {
        BoardOfUser.Role role;
        if (accessRight.equals("WRITE")) {
            role = BoardOfUser.Role.WRITE;
        } else {
            role = BoardOfUser.Role.READ;
        }
        BoardOfUser boardOfUser = new BoardOfUser(localUser, board, role);
        return boardOfUserRepository.save(boardOfUser);
    }

    public void deleteCollabById(BoardOfUser boardOfUser) {
        boardOfUserRepository.delete(boardOfUser);
    }
}
