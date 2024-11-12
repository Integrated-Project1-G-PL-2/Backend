package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.v3.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/v3/boards/{id}/tasks/{taskId}/files")
public class FileDataController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private BoardService boardService;

    @GetMapping("")
    public void getFileData(@PathVariable String id, @PathVariable int taskId, @RequestParam(value = "files", required = false) MultipartFile[] files) {
        boardService.addNewFileToTask(id, taskId, files);
    }

    @PostMapping("")
    public void createFileData() {

    }

    public void DeleteFileData() {

    }
}
