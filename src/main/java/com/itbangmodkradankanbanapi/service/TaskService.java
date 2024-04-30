package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class TaskService {
    @Autowired
    private  TaskRepository repository;

    public Task findTaskById(int id) throws ItemNotFoundException {
        Task task =  repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task "+ id +" does not exist !!!" ));
        return task;
    }

    public List<Task> findAllTask() {
        return repository.findAll();
    }
    @Transactional
    public Task createNewTask(Task task){
        return  repository.save(task);
    }




}
