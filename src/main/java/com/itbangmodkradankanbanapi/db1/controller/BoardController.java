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

    //tested
    @GetMapping("/{id}")
    public ResponseEntity<Object> getBoardById(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(boardService.getBoardById(token, id));
    }


    //tested
    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(boardService.getAllBoard(token));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewBoard(@Valid @RequestBody BoardDTO boardDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createNewBoard(boardDTO, token));
    }
}
