package com.itbangmodkradankanbanapi.controller;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.StatusService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/v2/statuses")
public class StatusController {
    @Autowired
    StatusService service;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask() {
        return ResponseEntity.ok(service.findAllStatus());
    }

    @PostMapping("")
    public ResponseEntity<Object> createNewStatus(@RequestBody Status status) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createNewStatus(status));
    }
    @PutMapping("/{id}")
    public  ResponseEntity<Status> updateTask(@PathVariable Integer id , @RequestBody Status status){
        Status updatedStatus = service.updateStatus(id,status);
        return ResponseEntity.ok().body(updatedStatus);
    }
    @DeleteMapping ("/{id}")
    public ResponseEntity<Object>  deleteTask(@PathVariable Integer id){
        service.deleteStatus(id);
        return  ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping ("/{id}/{newId}")
    public ResponseEntity<Object>  deleteTask(@PathVariable Integer id , @PathVariable Integer newId){
        service.deleteStatusAndTransfer(id,newId);
        return  ResponseEntity.ok().body(new HashMap<>());
    }
}
