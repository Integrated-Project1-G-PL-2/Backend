package com.itbangmodkradankanbanapi.db1.v3.controller;

import com.itbangmodkradankanbanapi.db1.v3.dto.CollabDTORequest;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v3/boards/{id}/collabs")
public class CollabController {
    @Autowired
    private BoardService boardService;

    @PostMapping()
    public ResponseEntity<Object> addCollabToBoard(@RequestHeader("Authorization") String token, @Valid @RequestBody CollabDTORequest collabDTORequest, @PathVariable String id) {
        return ResponseEntity.ok().body(boardService.sendInvitation(token, collabDTORequest, id));
    }

    @GetMapping()
    public ResponseEntity<Object> getAllCollabOfBoard(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(boardService.getAllCollabOfBoard(id));
    }

    @GetMapping("/{collabId}")
    public ResponseEntity<Object> getCollabOfBoardById(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable String collabId) {
        return ResponseEntity.ok(boardService.getCollabOfBoard(id, collabId));
    }

    @PatchMapping("/{collabId}")
    public ResponseEntity<Object> editCollabOfBoardById(@RequestHeader("Authorization") String token, @Valid @RequestBody CollabDTORequest collabDTORequest, @PathVariable String id, @PathVariable String collabId) {
        return ResponseEntity.ok(boardService.editCollab(collabDTORequest, id, collabId));
    }

    @DeleteMapping("/{collabId}")
    public ResponseEntity<Object> deleteCollabOfBoardById(@RequestHeader("Authorization") String token, @PathVariable String id, @PathVariable String collabId) {
        boardService.deleteCollab(token, id, collabId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
