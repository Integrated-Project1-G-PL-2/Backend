package com.itbangmodkradankanbanapi.db1.v2.repositories;


import com.itbangmodkradankanbanapi.db1.v2.entities.TaskV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepositoryV2 extends JpaRepository<TaskV2, Integer> {
    List<TaskV2> findAllByOrderByCreatedOnAsc();

    @Query("SELECT t FROM TaskV2 t JOIN t.status s WHERE s.name IN :statusNames ORDER BY :sortBy ASC")
    List<TaskV2> findAllByStatusNamesSorted(@Param("statusNames") List<String> statusNames, @Param("sortBy") String sortBy);
}
