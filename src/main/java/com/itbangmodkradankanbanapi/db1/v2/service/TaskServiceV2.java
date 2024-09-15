package com.itbangmodkradankanbanapi.db1.v2.service;


import com.itbangmodkradankanbanapi.db1.v2.dto.TaskDTOForAddV2;
import com.itbangmodkradankanbanapi.db1.v2.dto.TaskDTOV2;
import com.itbangmodkradankanbanapi.db1.v2.entities.StatusV2;
import com.itbangmodkradankanbanapi.db1.v2.entities.TaskV2;
import com.itbangmodkradankanbanapi.db1.v2.repositories.StatusRepositoryV2;
import com.itbangmodkradankanbanapi.db1.v2.repositories.TaskRepositoryV2;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskServiceV2 {
    @Autowired
    private TaskRepositoryV2 repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private StatusRepositoryV2 statusRepository;

    public TaskV2 findTaskById(int id) throws ItemNotFoundException {
        TaskV2 taskV2 = repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task " + id + " does not exist !!!"));
        return taskV2;
    }

    public List<TaskV2> findAllTask(List<String> statusNames, String sortBy) {
        List<TaskV2> taskV2List;
        if (statusNames == null || statusNames.isEmpty()) {
            taskV2List = repository.findAll();
        } else {
            taskV2List = repository.findAllByStatusNamesSorted(statusNames, sortBy);
        }

        return taskV2List;
    }

    @Transactional
    public TaskDTOV2 createNewTask(TaskDTOForAddV2 newTask) {
        StatusV2 statusObj = statusRepository.findById(newTask.getStatus()).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskV2 taskV2 = mapper.map(newTask, TaskV2.class);
        taskV2.setStatus(statusObj);
        TaskV2 savedTaskV2 = repository.saveAndFlush(taskV2);
        return mapper.map(savedTaskV2, TaskDTOV2.class);
    }

    @Transactional
    public TaskDTOV2 deleteTask(int id) {
        TaskV2 taskV2 = repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskDTOV2 taskDTO = mapper.map(taskV2, TaskDTOV2.class);
        repository.delete(taskV2);
        taskDTO.setDescription(null);
        return taskDTO;
    }

    @Transactional
    public TaskDTOV2 updateTask(Integer id, TaskDTOV2 taskDTO) {
        TaskV2 existingTaskV2 = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingTaskV2.setDescription(taskDTO.getDescription());
        existingTaskV2.setTitle(taskDTO.getTitle());
        existingTaskV2.setAssignees(taskDTO.getAssignees());
        existingTaskV2.setStatus(taskDTO.getStatus());
        TaskV2 savedTaskV2 = repository.save(existingTaskV2);
        TaskDTOV2 updateTaskDTO = mapper.map(savedTaskV2, TaskDTOV2.class);
        updateTaskDTO.setDescription(null);
        return updateTaskDTO;
    }
}

