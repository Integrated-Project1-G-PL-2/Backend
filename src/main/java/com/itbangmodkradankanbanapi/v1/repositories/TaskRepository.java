package com.itbangmodkradankanbanapi.v1.repositories;

import com.itbangmodkradankanbanapi.v1.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task , Integer> {
    List<Task> findAllByOrderByCreatedOnAsc();
}
