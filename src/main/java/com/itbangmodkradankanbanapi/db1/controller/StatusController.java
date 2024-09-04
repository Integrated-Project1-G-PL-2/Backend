package com.itbangmodkradankanbanapi.db1.controller;

import com.itbangmodkradankanbanapi.db1.dto.StatusDTO;
import com.itbangmodkradankanbanapi.db1.service.BoardService;
import com.itbangmodkradankanbanapi.db1.service.ListMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/v3/boards/{id}/statuses")
public class StatusController {
    @Autowired
    private BoardService boardService;

    // tested
    @GetMapping("/{statusId}")
    public ResponseEntity<Object> getStatusOfBoardById(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId) {
        return ResponseEntity.ok(boardService.getStatusById(id, token, statusId));
    }

    //tested
    @GetMapping("")
    public ResponseEntity<Object> getAllStatusOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(boardService.getAllStatus(token, id));
    }

    //tested
    @PostMapping("")
    public ResponseEntity<Object> addNewStatusToBoard(@Valid @RequestBody StatusDTO statusDTO, @RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewStatusToBoard(statusDTO, token, id));
    }


    @PutMapping("/{statusId}")
    public ResponseEntity<StatusDTO> editStatusOfBoard(@Valid @RequestBody StatusDTO statusDTO, @RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId) {
        return ResponseEntity.ok(boardService.editStatusOfBoard(statusDTO, token, id, statusId));
    }


    @DeleteMapping("/{statusId}")
    public ResponseEntity<Object> deleteStatusOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId) {
        boardService.deleteStatusOfBoard(token, id, statusId);
        return ResponseEntity.ok(new HashMap<>());
    }

    @DeleteMapping("/{statusId}/{newStatusId}")
    public ResponseEntity<Object> deleteThenTransferStatusOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable int statusId, @PathVariable int newStatusId) {
        boardService.deleteThenTranferStatusOfBoard(token, id, statusId, newStatusId);
        return ResponseEntity.ok(new HashMap<>());
    }
}
