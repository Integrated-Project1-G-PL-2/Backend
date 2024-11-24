package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.FilesDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.ListMapper;
import com.itbangmodkradankanbanapi.db1.v3.service.StorageService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
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
    public ResponseEntity<Object> getAllTaskOfBoard(@RequestParam(required = false) List<String> filterStatuses, @RequestParam(required = false) String sortBy, @PathVariable String id) {
        return ResponseEntity.ok(listMapper.mapList(boardService.getAllTask(filterStatuses, sortBy, id), TaskDTO.class, mapper));
    }

    // tested
    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTaskOfBoardById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.getTaskById(id, token, taskId));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewTaskToBoard(@Valid @RequestBody TaskDTOForAdd task, @RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewTaskToBoard(task, id, null));
    }

    //tested
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> editTaskOfBoard(@Valid @RequestPart("taskDetails") TaskDTOForAdd task, @PathVariable String id, @PathVariable int taskId, @RequestParam(value = "file", required = false) MultipartFile[] files) {
        if (files != null) {
            if (files.length > 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum file count is 10");
            }
            files = Arrays.stream(files)
                    .filter(file -> !file.isEmpty())
                    .toArray(MultipartFile[]::new);
            if (files.length > 0) {
                return ResponseEntity.status(HttpStatus.OK).body(boardService.editTaskOfBoard(task, id, taskId, files));
            }
        }
        return ResponseEntity.ok(boardService.editTaskOfBoard(task, id, taskId, null));
    }


    //tested
    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDTO> deleteTaskOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.deleteTaskOfBoard(id, taskId));
    }

    @DeleteMapping("/{taskId}/files")
    public ResponseEntity<TaskDTO> deleteFileFromTask(@RequestBody FilesDTO filesDTO, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.deleteFileFromTask(id, taskId, filesDTO));
    }


}
