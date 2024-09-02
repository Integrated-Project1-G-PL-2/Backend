package com.itbangmodkradankanbanapi.db1.controller;

import com.itbangmodkradankanbanapi.db1.dto.BoardDTO;
import com.itbangmodkradankanbanapi.db1.dto.StatusDTO;
import com.itbangmodkradankanbanapi.db1.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.service.BoardService;
import com.itbangmodkradankanbanapi.db1.service.ListMapper;
import com.itbangmodkradankanbanapi.db1.service.TaskService;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

    //tested
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Object> getAllTaskOfBoard(@RequestParam(required = false) List<String> filterStatuses, @RequestParam(required = false) String sortBy, @RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(listMapper.mapList(boardService.getAllTask(filterStatuses, sortBy, token, id), TaskDTO.class, mapper));
    }

    //tested
    @GetMapping("/{id}/statuses")
    public ResponseEntity<Object> getAllStatusOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(boardService.getAllStatus(token, id));
    }

    //tested
    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoardById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(boardService.getBoardById(token, id));
    }

    // tested
    @GetMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<Object> getTaskOfBoardById(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.getTaskById(id, token, taskId));
    }

    // tested
    @GetMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> getStatusOfBoardById(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId) {
        return ResponseEntity.ok(boardService.getStatusById(id, token, statusId));
    }

    //tested
    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(boardService.getAllBoard(token));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewBoard(@RequestBody BoardDTO boardDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(boardService.createNewBoard(boardDTO, token));
    }

    //tested
    @PostMapping("/{id}/tasks")
    public ResponseEntity<Object> addNewTaskToBoard(@Valid @RequestBody TaskDTOForAdd task, @RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewTaskToBoard(task, token, id));
    }

    //tested
    @PostMapping("/{id}/statuses")
    public ResponseEntity<Object> addNewStatusToBoard(@Valid @RequestBody StatusDTO statusDTO, @RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewStatusToBoard(statusDTO, token, id));
    }

    //tested
    @PutMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> editTaskOfBoard(@Valid @RequestBody TaskDTO task, @RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.editTaskOfBoard(task, token, id, taskId));
    }

    @PutMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<StatusDTO> editStatusOfBoard(@Valid @RequestBody StatusDTO statusDTO, @RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId) {
        return ResponseEntity.ok(boardService.editStatusOfBoard(statusDTO, token, id, statusId));
    }

    //tested
    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> deleteTaskOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.deleteTaskOfBoard(token, id, taskId));
    }

    @DeleteMapping("/{id}/statuses/{statusId}")
    public ResponseEntity<Object> deleteStatusOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId) {
        boardService.deleteStatusOfBoard(token, id, statusId);
        return ResponseEntity.ok(new HashMap<>());
    }


}
