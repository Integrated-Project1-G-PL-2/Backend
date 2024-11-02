package com.itbangmodkradankanbanapi.db1.v3.controller;


import com.itbangmodkradankanbanapi.db1.v3.entities.Invitation;
import com.itbangmodkradankanbanapi.db1.v3.repositories.InvitationRepository;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.v3.service.InvitationService;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v3/boards/{id}/invitation")
public class InvitationController {
    @Autowired
    BoardService boardService;

    @Autowired
    InvitationService invitationService;


    @PostMapping("")
    public ResponseEntity<Object> AcceptInvitation(@PathVariable String id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewCollab(token, id));
    }

    @DeleteMapping("")
    public ResponseEntity<Object> DeclinedInvitation(@PathVariable String id, @RequestHeader("Authorization") String token) {
        invitationService.deleteInvitationFormId(token, id);
        return ResponseEntity.ok(null);
    }
}
