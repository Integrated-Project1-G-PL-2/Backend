package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.Board;
import com.itbangmodkradankanbanapi.db1.entities.BoardOfUser;
import com.itbangmodkradankanbanapi.db1.entities.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardOfUserRepository extends JpaRepository<BoardOfUser, BoardOfUser.BoardUserKey> {
   BoardOfUser findBoardOfUserByLocalUserAndBoard(LocalUser localUser, Board board);
}
