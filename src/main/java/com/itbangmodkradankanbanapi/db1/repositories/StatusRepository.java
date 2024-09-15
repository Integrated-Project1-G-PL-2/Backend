package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.Board;
import com.itbangmodkradankanbanapi.db1.entities.Status;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findAllByNameIgnoreCaseAndBoard(String name, Board board);

    List<Status> findAllByBoard(Board board);

    Optional<Status> findByBoard_IdAndId(String board_id, int id);

    Optional<Status> findByNameAndBoardIsNull(String name);
}
