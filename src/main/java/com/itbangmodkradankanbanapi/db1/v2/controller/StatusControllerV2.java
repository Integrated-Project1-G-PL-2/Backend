package com.itbangmodkradankanbanapi.db1.v2.controller;

import com.itbangmodkradankanbanapi.db1.v2.entities.StatusV2;
import com.itbangmodkradankanbanapi.db1.v2.dto.StatusDTOV2;
import com.itbangmodkradankanbanapi.db1.ListMapper;
import com.itbangmodkradankanbanapi.db1.v2.service.StatusServiceV2;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/v2/statuses")
public class StatusControllerV2 {
    @Autowired
    StatusServiceV2 service;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask() {
        return ResponseEntity.ok(service.findAllStatus());
    }

    @PostMapping("")
    public ResponseEntity<Object> createNewStatus(@Valid @RequestBody StatusDTOV2 status) {
        StatusDTOV2 createdStatus = service.createNewStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusDTOV2> updateTask(@PathVariable Integer id, @Valid @RequestBody StatusDTOV2 status) {
        StatusDTOV2 updatedStatus = service.updateStatus(id, status);
        return ResponseEntity.ok().body(updatedStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable Integer id) {
        service.deleteStatus(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<Object> deleteTask(@PathVariable Integer id, @PathVariable Integer newId) {
        service.deleteStatusAndTransfer(id, newId);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @GetMapping("/{id}")
    public StatusV2 getStatusById(@PathVariable Integer id) {
        return service.findStatusById(id);
    }
}
