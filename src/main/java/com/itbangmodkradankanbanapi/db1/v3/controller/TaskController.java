package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.ListMapper;
import com.itbangmodkradankanbanapi.db1.v3.service.FilesDataService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private FilesDataService filesDataService;

    //tested
    @GetMapping("")
    public ResponseEntity<Object> getAllTaskOfBoard(@RequestParam(required = false) List<String> filterStatuses, @RequestParam(required = false) String sortBy, @RequestHeader(value = "Authorization", required = false) String token, @PathVariable String id) {
        return ResponseEntity.ok(listMapper.mapList(boardService.getAllTask(filterStatuses, sortBy, token, id), TaskDTO.class, mapper));
    }

    // tested
    @GetMapping("/{taskId}")
    public ResponseEntity<Object> getTaskOfBoardById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.getTaskById(id, token, taskId));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewTaskToBoard(@PathVariable String id, @Valid @RequestPart("taskDetails") TaskDTOForAdd task,
                                                    @RequestParam(value = "file", required = false) MultipartFile file) {
//        if (file != null) {
//            String contentType = file.getContentType();
//            if (contentType == null || !contentType.startsWith("image/")) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format");
//            }
//            String fileUrl = filesDataService.uploadFile(file);
//            book.setPath(fileUrl);
//        }
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewTaskToBoard(task, id));
    }

    //tested
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> editTaskOfBoard(@Valid @RequestBody(required = false) TaskDTOForAdd task, @RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.editTaskOfBoard(task, token, id, taskId));
    }

    //tested
    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDTO> deleteTaskOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int taskId) {
        return ResponseEntity.ok(boardService.deleteTaskOfBoard(token, id, taskId));
    }
}
