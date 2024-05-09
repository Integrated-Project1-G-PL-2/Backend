package com.itbangmodkradankanbanapi.controller;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.TaskService;
import com.itbangmodkradankanbanapi.service.TaskV2Service;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<Object> getAllTask() {
        return ResponseEntity.ok(service.findAllTask());
    }


    }
