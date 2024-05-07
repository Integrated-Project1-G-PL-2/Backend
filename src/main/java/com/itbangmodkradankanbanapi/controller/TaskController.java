package com.itbangmodkradankanbanapi.controller;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.TaskService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public  ResponseEntity<TaskDTO> createNewTask(@Valid @RequestBody TaskDTO task){
        TaskDTO createdTask = service.createNewTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @DeleteMapping ("/{id}")
    public  ResponseEntity<TaskDTO> deleteTask(@PathVariable Integer id){
        TaskDTO task = service.deleteTask(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id ,@RequestBody TaskDTO taskDTO){
        TaskDTO updatedTaskDTO = service.updateTask(id,taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedTaskDTO);
    }
    }
