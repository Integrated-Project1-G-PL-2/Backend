package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByOrderByCreatedOnAsc();

    @Query("SELECT t FROM Task t JOIN t.status s  JOIN t.board b WHERE s.name IN :statusNames  AND b.id = :boardId ORDER BY :sortBy ASC")
    List<Task> findAllByStatusNamesSorted(@Param("statusNames") List<String> statusNames, @Param("sortBy") String sortBy, @Param("boardId") String boardId);

    @Query(value = "SELECT * FROM tasksv2 WHERE boardId = :boardId", nativeQuery = true)
    List<Task> findAllTaskByBoardId(@Param("boardId") String boardId);

    Optional<Task> findByBoard_IdAndId(String board_id, int id);
}
