package com.itbangmodkradankanbanapi.repositories;

import com.itbangmodkradankanbanapi.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TaskRepository extends JpaRepository<Task , Integer> {
}
