package com.itbangmodkradankanbanapi.db1.controller;

import com.itbangmodkradankanbanapi.db1.dto.StatusDTO;
import com.itbangmodkradankanbanapi.db1.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.service.BoardService;
import com.itbangmodkradankanbanapi.db1.service.ListMapper;
import com.itbangmodkradankanbanapi.db1.service.TaskService;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/tasks")
    public ResponseEntity<Object> getAllTask(@RequestParam(required = false) List<String> filterStatuses , @RequestParam(required = false) String sortBy, @RequestHeader("Authorization") String token,@PathVariable String id){
        return  ResponseEntity.ok(listMapper.mapList(boardService.getAllTask(filterStatuses,sortBy,token,id), TaskDTO.class, mapper));
    }
//
//    @PutMapping("")
//    public ResponseEntity<Object> addNewTask(){
//
//    }
}
