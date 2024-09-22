package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardOfUserRepository extends JpaRepository<BoardOfUser, BoardOfUser.BoardUserKey> {
    BoardOfUser findBoardOfUserByLocalUserAndBoard(LocalUser localUser, Board board);

    List<BoardOfUser> findAllByLocalUserAndRole(LocalUser localUser, BoardOfUser.Role role);

    BoardOfUser findBoardOfUserByBoardAndRole(Board board, BoardOfUser.Role role);
}
