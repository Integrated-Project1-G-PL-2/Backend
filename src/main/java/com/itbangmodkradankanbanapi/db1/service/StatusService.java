package com.itbangmodkradankanbanapi.db1.service;


import com.itbangmodkradankanbanapi.db1.dto.StatusDTO;
import com.itbangmodkradankanbanapi.db1.entities.Board;
import com.itbangmodkradankanbanapi.db1.entities.Status;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.db1.entities.Task;
import com.itbangmodkradankanbanapi.db1.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.db1.repositories.TaskRepository;
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

    public List<Status> findAllStatus(String boardId) {
        return repository.findAllStatusByBoardId(boardId);
    }

    @Transactional
    public StatusDTO createNewStatus(StatusDTO newStatus, Board board) {
        List<Status> statusList = repository.findAllByNameIgnoreCaseAndBoard(newStatus.getName(), board);
        if (!statusList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not duplicate name");
        }
        Status mapStatus = mapper.map(newStatus, Status.class);
        mapStatus.setBoard(board);
        Status savedStatus = repository.saveAndFlush(mapStatus);
        return mapper.map(savedStatus, StatusDTO.class);
    }

    public Status findStatusById(String boardId, int id) throws ItemNotFoundException {
        Status status = repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        return status;
    }

    @Transactional
    public StatusDTO updateStatus(String boardId, Integer id, StatusDTO status) {
        Status existingStatus = repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingStatus.setName(status.getName());
        existingStatus.setDescription(status.getDescription());
        Status updateStatus = repository.save(existingStatus);
        return mapper.map(updateStatus, StatusDTO.class);
    }

    // cant delete if it used as FK
    @Transactional
    public void deleteStatus(String boardId, int id) {
        Status status = repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        repository.delete(status);
    }


    @Transactional
    public void deleteStatusAndTransfer(String boardId, Integer id, Integer newId) {
        Status status = repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        Status transferStatus = repository.findByBoard_IdAndId(boardId, newId).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        List<Task> taskV2V2s = status.getTaskV2s();
        taskV2V2s.forEach(task -> task.setStatus(transferStatus));
        taskRepository.saveAll(taskV2V2s);
        repository.delete(status);
    }

}
