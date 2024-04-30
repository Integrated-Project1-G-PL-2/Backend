package com.itbangmodkradankanbanapi.controller;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.exception.ErrorResponse;
import com.itbangmodkradankanbanapi.exception.GlobalExceptionHandler;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    TaskService service;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask() {
        return ResponseEntity.ok(listMapper.mapList(service.findAllTask(), TaskDTO.class, mapper));
    }

    @GetMapping("/test")
    public ResponseEntity<Object> getAllTask1() {
        return ResponseEntity.ok(service.findAllTask());
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Integer id){
        return service.findTaskById(id);
    }

    @PostMapping("")
    public  Task createNewTask(@RequestBody Task task){
        return  service.createNewTask(task);

    }

}
