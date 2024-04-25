package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private  TaskRepository repository;

    public Task findTaskById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task " + id + " does not exist !!!!"));
    }

    public List<Task> findAllTask() {
        return repository.findAll();
    }
    @Transactional
    public Task createNewTask(Task task){
        return  repository.save(task);
    }



}
