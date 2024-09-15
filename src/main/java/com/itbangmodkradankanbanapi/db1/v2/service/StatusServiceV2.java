package com.itbangmodkradankanbanapi.db1.v2.service;

import com.itbangmodkradankanbanapi.db1.v2.entities.TaskV2;
import com.itbangmodkradankanbanapi.db1.v2.dto.StatusDTOV2;
import com.itbangmodkradankanbanapi.db1.v2.entities.StatusV2;
import com.itbangmodkradankanbanapi.db1.v2.repositories.StatusRepositoryV2;
import com.itbangmodkradankanbanapi.db1.v2.repositories.TaskRepositoryV2;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class StatusServiceV2 {
    @Autowired
    private StatusRepositoryV2 repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private TaskRepositoryV2 taskRepository;

    public List<StatusV2> findAllStatus() {
        return repository.findAll();
    }

    @Transactional
    public StatusDTOV2 createNewStatus(StatusDTOV2 newStatus) {

        List<StatusV2> statusList = repository.findAllByNameIgnoreCase(newStatus.getName());
        if (!statusList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not duplicate name");
        }
        StatusV2 mapStatus = mapper.map(newStatus, StatusV2.class);
        StatusV2 savedStatus = repository.saveAndFlush(mapStatus);
        return mapper.map(savedStatus, StatusDTOV2.class);
    }

    public StatusV2 findStatusById(int id) throws ItemNotFoundException {
        StatusV2 status = repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        return status;
    }

    @Transactional
    public StatusDTOV2 updateStatus(Integer id, StatusDTOV2 status) {
        if (id == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be 1");
        }
        StatusV2 existingStatus = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingStatus.setName(status.getName());
        existingStatus.setDescription(status.getDescription());
        StatusV2 updateStatus = repository.save(existingStatus);
        return mapper.map(updateStatus, StatusDTOV2.class);
    }

    @Transactional
    public void deleteStatus(Integer id) {
        System.out.println(id);
        StatusV2 status = repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

        repository.delete(status);
    }

    @Transactional
    public void deleteStatusAndTransfer(Integer id, Integer newId) {
        StatusV2 status = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        StatusV2 transferStatus = repository.findById(newId)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

        List<TaskV2> taskV2V2s = status.getTaskV2s();
        taskV2V2s.forEach(task -> task.setStatus(transferStatus));
        taskRepository.saveAll(taskV2V2s);
        repository.delete(status);
    }

}
