package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.Status;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    List<Task> findAllByOrderByCreatedOnAsc();
    @Query("SELECT t FROM Task t JOIN t.status s  JOIN Board b WHERE s.name IN :statusNames  AND b.id = :boardId ORDER BY :sortBy ASC")
    List<Task> findAllByStatusNamesSorted(@Param("statusNames") List<String> statusNames , @Param("sortBy") String sortBy,@Param("boardId") String boardId);

    List<Task> findAllByBoardIs(String id);
}
