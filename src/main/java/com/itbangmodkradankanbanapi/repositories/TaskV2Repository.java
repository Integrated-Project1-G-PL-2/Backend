package com.itbangmodkradankanbanapi.repositories;

import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskV2Repository extends JpaRepository<TaskV2 , Integer> {
    List<TaskV2> findAllByOrderByCreatedOnAsc();

}
