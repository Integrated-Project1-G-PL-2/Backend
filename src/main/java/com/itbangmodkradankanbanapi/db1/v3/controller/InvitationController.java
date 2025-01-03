package com.itbangmodkradankanbanapi.db1.v3.controller;


import com.itbangmodkradankanbanapi.db1.v3.dto.CollabDTORequest;
import com.itbangmodkradankanbanapi.db1.v3.entities.Invitation;
import com.itbangmodkradankanbanapi.db1.v3.repositories.InvitationRepository;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db1.v3.service.InvitationService;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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


    @GetMapping("")
    public ResponseEntity<Object> checkInvitation(@PathVariable String id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(invitationService.checkInvitation(token, id));
    }


    @PostMapping("")
    public ResponseEntity<Object> acceptInvitation(@PathVariable String id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.addNewCollab(token, id));
    }

    @PutMapping("/{collabId}")
    public ResponseEntity<Object> editInvitation(@PathVariable String id, @RequestHeader("Authorization") String token, @Valid @RequestBody CollabDTORequest collabDTORequest, @PathVariable String collabId) {
        return ResponseEntity.ok(invitationService.editInvitation(token, id, collabDTORequest, collabId));
    }

    @DeleteMapping("")
    public ResponseEntity<Object> declinedInvitation(@PathVariable String id, @RequestHeader("Authorization") String token) {
        invitationService.deleteInvitationFormId(token, id);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{collabId}")
    public ResponseEntity<Object> cancelInvitation(@PathVariable String id, @RequestHeader("Authorization") String token, @PathVariable String collabId) {
        invitationService.cancelInvitationFormId(token, id, collabId);
        return ResponseEntity.ok(null);
    }
}
