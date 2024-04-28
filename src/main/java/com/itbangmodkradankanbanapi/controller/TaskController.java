package com.itbangmodkradankanbanapi.controller;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.exception.ErrorResponse;
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

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleItemNotFound(Exception ex, WebRequest request) {
        String id = request.getDescription(false).split("/")[4];
        ErrorResponse er = new ErrorResponse(Timestamp.from(Instant.now()),HttpStatus.NOT_FOUND.value(),"Not Found", "Task "+ id + " dose not exist !!!!", request.getDescription(false).substring(4));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

}
