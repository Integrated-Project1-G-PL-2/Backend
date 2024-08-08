package com.itbangmodkradankanbanapi.service;


import com.itbangmodkradankanbanapi.dto.StatusDTO;
import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TaskRepository taskRepository;

    public List<Status> findAllStatus() {
        return repository.findAll();
    }

    @Transactional
    public StatusDTO createNewStatus(StatusDTO newStatus)  {

            List<Status> statusList = repository.findAllByNameIgnoreCase(newStatus.getName());
            if(!statusList.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not duplicate name");
            }
            Status mapStatus = mapper.map(newStatus, Status.class);
            Status savedStatus = repository.saveAndFlush(mapStatus);
            return  mapper.map(savedStatus, StatusDTO.class);
    }

    public Status findStatusById(int id) throws ItemNotFoundException {
        Status status = repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        return status;
    }

    @Transactional
    public StatusDTO updateStatus(Integer id, StatusDTO status) {
        if(id == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be 1");
        }
        Status existingStatus = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingStatus.setName(status.getName());
        existingStatus.setDescription(status.getDescription());
        Status updateStatus = repository.save(existingStatus);
        return mapper.map(updateStatus ,StatusDTO.class);
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

        List<Task> taskV2V2s = status.getTaskV2V2s();
        taskV2V2s.forEach(task -> task.setStatus(transferStatus));
        taskRepository.saveAll(taskV2V2s);
        repository.delete(status);
    }

}
