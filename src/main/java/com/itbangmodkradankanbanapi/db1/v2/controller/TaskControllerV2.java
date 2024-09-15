package com.itbangmodkradankanbanapi.db1.v2.controller;


import com.itbangmodkradankanbanapi.db1.ListMapper;
import com.itbangmodkradankanbanapi.db1.v2.dto.TaskDTOForAddV2;
import com.itbangmodkradankanbanapi.db1.v2.dto.TaskDTOV2;
import com.itbangmodkradankanbanapi.db1.v2.entities.TaskV2;
import com.itbangmodkradankanbanapi.db1.v2.service.TaskServiceV2;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v2/tasks")
public class TaskControllerV2 {
    @Autowired
    TaskServiceV2 service;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(@RequestParam(required = false) List<String> filterStatuses, @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(listMapper.mapList(service.findAllTask(filterStatuses, sortBy), TaskDTOV2.class, mapper));
    }

    @GetMapping("/{id}")
    public TaskV2 getTaskById(@PathVariable Integer id) {
        return service.findTaskById(id);
    }

    @PostMapping("")
    public ResponseEntity<TaskDTOV2> createNewTask(@Valid @RequestBody TaskDTOForAddV2 task) {
        TaskDTOV2 createdTask = service.createNewTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskDTOV2> deleteTask(@PathVariable Integer id) {
        TaskDTOV2 task = service.deleteTask(id);
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTOV2> updateTask(@PathVariable Integer id, @RequestBody TaskDTOV2 taskDTO) {
        TaskDTOV2 updatedTaskDTO = service.updateTask(id, taskDTO);
        return ResponseEntity.ok().body(updatedTaskDTO);
    }
}
