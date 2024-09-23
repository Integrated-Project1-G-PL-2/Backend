package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.BoardDTO;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.exception.InvalidRequestField;
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
    public ResponseEntity<Object> getBoardById(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String id) {
        return ResponseEntity.ok(boardService.getBoardById(token, id));
    }


    //tested
    @GetMapping("")
    public ResponseEntity<Object> getAllBoard(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(boardService.getAllBoard(token));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewBoard(@Valid @RequestBody(required = false) BoardDTO boardDTO, @RequestHeader("Authorization") String token) {
        if (boardDTO == null) {
            throw new InvalidRequestField(HttpStatus.BAD_REQUEST, "Invalid request body");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createNewBoard(boardDTO, token));
    }

    //tested
    @PatchMapping("/{id}")
    public ResponseEntity<Object> editVisibilityBoard(@Valid @RequestBody(required = false) BoardDTO boardDTO, @RequestHeader("Authorization") String token, @PathVariable String id) {
        if (boardDTO == null) {
            throw new InvalidRequestField(HttpStatus.BAD_REQUEST, "Invalid request body");
        }
        return ResponseEntity.ok(boardService.editBoard(boardDTO, token, id));
    }

//    @PostMapping("/{id}")
//    public ResponseEntity<Object> grantPrivilegeToBoard(@Valid @RequestBody(required = false) BoardDTO boardDTO, @RequestHeader("Authorization") String token, @PathVariable String id) {
//        if (boardDTO == null) {
//            throw new InvalidRequestField(HttpStatus.BAD_REQUEST, "Invalid request body");
//        }
//        return ResponseEntity.ok(boardService.grantPrivilegeToBoard(boardDTO, token, id));
//    }

}
