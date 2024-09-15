package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findAllByNameIgnoreCaseAndBoard(String name, Board board);

    List<Status> findAllByBoard(Board board);

    Optional<Status> findByBoard_IdAndId(String board_id, int id);

    Optional<Status> findByNameAndBoardIsNull(String name);
}
