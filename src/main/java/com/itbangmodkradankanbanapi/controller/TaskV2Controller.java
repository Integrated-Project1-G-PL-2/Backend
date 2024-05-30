package com.itbangmodkradankanbanapi.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.dto.TaskV2DTO;
import com.itbangmodkradankanbanapi.dto.TaskV2DTOForAdd;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.TaskService;
import com.itbangmodkradankanbanapi.service.TaskV2Service;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://intproj23.sit.kmutt.ac.th", "http://localhost:5173"})
@RestController
@RequestMapping("/v2/tasks")
public class TaskV2Controller {
    @Autowired
    TaskV2Service service;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

   @GetMapping("")
    public ResponseEntity<Object> getAllTask(@RequestParam(required = false) List<String> filterStatuses , @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(listMapper.mapList(service.findAllTask(filterStatuses,sortBy), TaskV2DTO.class, mapper));
    }

    @GetMapping("/{id}")
    public TaskV2 getTaskById(@PathVariable Integer id){
        return service.findTaskById(id);
    }

    @PostMapping("")
    public  ResponseEntity<TaskV2DTO> createNewTask(@Valid @RequestBody TaskV2DTOForAdd task){
        TaskV2DTO createdTask = service.createNewTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @DeleteMapping ("/{id}")
    public  ResponseEntity<TaskV2DTO> deleteTask(@PathVariable Integer id){
        TaskV2DTO task = service.deleteTask(id);
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<TaskV2DTO> updateTask(@PathVariable Integer id ,@RequestBody TaskV2DTO taskDTO){
        TaskV2DTO updatedTaskDTO = service.updateTask(id,taskDTO);
        return ResponseEntity.ok().body(updatedTaskDTO);
    }
    }
