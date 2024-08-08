package com.itbangmodkradankanbanapi.v2.service;

import com.itbangmodkradankanbanapi.v2.dto.TaskV2DTO;
import com.itbangmodkradankanbanapi.v2.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.v2.entities.Status;
import com.itbangmodkradankanbanapi.v2.entities.TaskV2;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.v2.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.v2.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskV2Service {
    @Autowired
    private TaskV2Repository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private StatusRepository statusRepository;
    public TaskV2 findTaskById(int id) throws ItemNotFoundException {
        TaskV2 taskV2 =  repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task "+ id +" does not exist !!!" ));
        return taskV2;
    }

    public List<TaskV2> findAllTask(List<String> statusNames , String  sortBy) {
        List<TaskV2> taskV2List;
        if (statusNames == null || statusNames.isEmpty()) {
            taskV2List = repository.findAll();
        } else {
            taskV2List = repository.findAllByStatusNamesSorted(statusNames, sortBy);
        }

        return taskV2List;
    }
    @Transactional
    public TaskV2DTO createNewTask(TaskDTOForAdd newTask)  {
            Status statusObj = statusRepository.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            TaskV2 taskV2 = mapper.map(newTask, TaskV2.class);
            taskV2.setStatus(statusObj);
            TaskV2 savedTaskV2 = repository.saveAndFlush(taskV2);
            return mapper.map(savedTaskV2, TaskV2DTO.class);
    }

    @Transactional
    public TaskV2DTO deleteTask(int id){
        TaskV2 taskV2 =  repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskV2DTO taskV2DTO = mapper.map(taskV2, TaskV2DTO.class);
        repository.delete(taskV2);
        taskV2DTO.setDescription(null);
        return taskV2DTO;
    }

    @Transactional
    public TaskV2DTO updateTask(Integer id, TaskV2DTO taskV2DTO) {
        TaskV2 existingTaskV2 = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingTaskV2.setDescription(taskV2DTO.getDescription());
        existingTaskV2.setTitle(taskV2DTO.getTitle());
        existingTaskV2.setAssignees(taskV2DTO.getAssignees());
        existingTaskV2.setStatus(taskV2DTO.getStatus());
        TaskV2 savedTaskV2 = repository.save(existingTaskV2);
        TaskV2DTO updateTaskV2DTO = mapper.map(savedTaskV2, TaskV2DTO.class);
        updateTaskV2DTO.setDescription(null);
        return updateTaskV2DTO;
    }
}

