package com.itbangmodkradankanbanapi.db1.controller;

import com.itbangmodkradankanbanapi.db1.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import com.itbangmodkradankanbanapi.db1.service.ListMapper;
import com.itbangmodkradankanbanapi.db1.service.TaskService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskService service;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

   @GetMapping("")
    public ResponseEntity<Object> getAllTask(@RequestParam(required = false) List<String> filterStatuses , @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(listMapper.mapList(service.findAllTask(filterStatuses,sortBy), TaskDTO.class, mapper));
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Integer id){
        return service.findTaskById(id);
    }

    @PostMapping("")
    public  ResponseEntity<TaskDTO> createNewTask(@Valid @RequestBody TaskDTOForAdd task){
        TaskDTO createdTask = service.createNewTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @DeleteMapping ("/{id}")
    public  ResponseEntity<TaskDTO> deleteTask(@PathVariable Integer id){
        TaskDTO task = service.deleteTask(id);
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id , @RequestBody TaskDTO taskDTO){
        TaskDTO updatedTaskDTO = service.updateTask(id, taskDTO);
        return ResponseEntity.ok().body(updatedTaskDTO);
    }
    }
