package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.dto.TaskV2DTO;
import com.itbangmodkradankanbanapi.dto.TaskV2DTOForAdd;
import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.repositories.TaskRepository;
import com.itbangmodkradankanbanapi.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskV2Service {
    @Autowired
    private TaskV2Repository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private StatusRepository statusRepository;
    public TaskV2 findTaskById(int id) throws ItemNotFoundException {
        TaskV2 task =  repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task "+ id +" does not exist !!!" ));
        return task;
    }

    public List<TaskV2> findAllTask(List<String> statusNames , String  sortBy) {
        List<TaskV2> taskList;
        if (statusNames == null || statusNames.isEmpty()) {
            taskList = repository.findAll();
        } else {
            taskList = repository.findAllByStatusNamesSorted(statusNames, sortBy);
        }

        return taskList;
    }
    @Transactional
    public TaskV2DTO createNewTask(TaskV2DTOForAdd newTask)  {
            Status statusObj = statusRepository.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            TaskV2 task = mapper.map(newTask, TaskV2.class);
            task.setStatus(statusObj);
            TaskV2 savedTask = repository.saveAndFlush(task);
            return mapper.map(savedTask, TaskV2DTO.class);
    }

    @Transactional
    public TaskV2DTO deleteTask(int id){
        TaskV2 task =  repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskV2DTO taskDTO = mapper.map(task, TaskV2DTO.class);
        repository.delete(task);
        taskDTO.setDescription(null);
        return taskDTO;
    }

    @Transactional
    public TaskV2DTO updateTask(Integer id, TaskV2DTO taskDTO) {
        TaskV2 existingTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setAssignees(taskDTO.getAssignees());
        existingTask.setStatus(taskDTO.getStatus());
        TaskV2 savedTask = repository.save(existingTask);
        TaskV2DTO updateTaskDTO = mapper.map(savedTask, TaskV2DTO.class);
        updateTaskDTO.setDescription(null);
        return  updateTaskDTO;
    }
}

