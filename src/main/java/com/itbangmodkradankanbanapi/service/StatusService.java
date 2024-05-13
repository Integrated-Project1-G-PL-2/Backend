package com.itbangmodkradankanbanapi.service;


import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class StatusService {
    @Autowired
    private StatusRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private TaskV2Repository taskV2Repository;

    public List<Status> findAllStatus() {
        return repository.findAll();
    }

    @Transactional
    public Status createNewStatus(Status newStatus) throws DataAccessException {
        try {
            newStatus.setName(newStatus.getName().trim());
            if(newStatus.getDescription() != null) {
                newStatus.setDescription(newStatus.getDescription().trim());
            }
            List<Status> statusList = repository.findAllByNameIgnoreCase(newStatus.getName());
            if(!statusList.isEmpty()){
                newStatus.setName("No Status");
            }
            return repository.save(newStatus);
        } catch (DataAccessException ex) {
            String errorMessage = "Failed to create new task: " + ex.getMessage();
            throw new DataAccessException(errorMessage, ex.getCause()) {
            };
        }
    }

    public Status findStatusById(int id) throws ItemNotFoundException {
        Status status = repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        return status;
    }

    @Transactional
    public Status updateStatus(Integer id, Status status) {
        if(id == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be 1");
        }
        Status existingStatus = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingStatus.setName(status.getName().trim());
        if(status.getDescription() != null){
        existingStatus.setDescription(status.getDescription().trim());
        }else {
            existingStatus.setDescription(null);
        }
        Status updateStatus = repository.save(existingStatus);
        return updateStatus;
    }

    @Transactional
    public void deleteStatus(Integer id) {
        System.out.println(id);
        Status status = repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

        repository.delete(status);
    }

    @Transactional
    public void deleteStatusAndTransfer(Integer id, Integer newId) {
        Status status = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        Status transferStatus = repository.findById(newId)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

        List<TaskV2> taskV2s = status.getTaskV2s();
        taskV2s.forEach(task -> task.setStatus(transferStatus));
        taskV2Repository.saveAll(taskV2s);
        repository.delete(status);
    }

}
