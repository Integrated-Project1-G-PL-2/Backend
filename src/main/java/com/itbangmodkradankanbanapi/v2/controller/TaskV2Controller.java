package com.itbangmodkradankanbanapi.v2.controller;

import com.itbangmodkradankanbanapi.v2.dto.TaskV2DTO;
import com.itbangmodkradankanbanapi.v2.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.v2.entities.TaskV2;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.v2.service.TaskV2Service;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public  ResponseEntity<TaskV2DTO> createNewTask(@Valid @RequestBody TaskDTOForAdd task){
        TaskV2DTO createdTask = service.createNewTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @DeleteMapping ("/{id}")
    public  ResponseEntity<TaskV2DTO> deleteTask(@PathVariable Integer id){
        TaskV2DTO task = service.deleteTask(id);
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<TaskV2DTO> updateTask(@PathVariable Integer id , @RequestBody TaskV2DTO taskV2DTO){
        TaskV2DTO updatedTaskV2DTO = service.updateTask(id, taskV2DTO);
        return ResponseEntity.ok().body(updatedTaskV2DTO);
    }
    }
