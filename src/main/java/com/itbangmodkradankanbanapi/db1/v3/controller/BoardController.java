package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.BoardDTO;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
