package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.ListMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v3/boards/{id}/tasks")
public class TaskController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

    //tested
    @GetMapping("")
    public ResponseEntity<Object> getAllTaskOfBoard(@RequestParam(required = false) List<String> filterStatuses, @RequestParam(required = false) String sortBy, @RequestHeader(value = "Authorization", required = false) String token, @PathVariable String id) {
        System.out.println("test");
        return ResponseEntity.ok(listMapper.mapList(boardService.getAllTask(filterStatuses, sortBy, token, id), TaskDTO.class, mapper));
    }

    // tested
    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTaskOfBoardById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.getTaskById(id, token, taskId));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewTaskToBoard(@Valid @RequestBody TaskDTOForAdd task, @RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewTaskToBoard(task, token, id));
    }

    //tested
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> editTaskOfBoard(@Valid @RequestBody TaskDTOForAdd task, @RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.editTaskOfBoard(task, token, id, taskId));
    }

    //tested
    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDTO> deleteTaskOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.deleteTaskOfBoard(token, id, taskId));
    }
}
