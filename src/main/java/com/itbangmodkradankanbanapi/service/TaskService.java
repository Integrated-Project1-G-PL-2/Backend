package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private StatusRepository statusRepository;
    public Task findTaskById(int id) throws ItemNotFoundException {
        Task taskV2 =  repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task "+ id +" does not exist !!!" ));
        return taskV2;
    }

    public List<Task> findAllTask(List<String> statusNames , String  sortBy) {
        List<Task> taskV2List;
        if (statusNames == null || statusNames.isEmpty()) {
            taskV2List = repository.findAll();
        } else {
            taskV2List = repository.findAllByStatusNamesSorted(statusNames, sortBy);
        }

        return taskV2List;
    }
    @Transactional
    public TaskDTO createNewTask(TaskDTOForAdd newTask)  {
            Status statusObj = statusRepository.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            Task taskV2 = mapper.map(newTask, Task.class);
            taskV2.setStatus(statusObj);
            Task savedTaskV2 = repository.saveAndFlush(taskV2);
            return mapper.map(savedTaskV2, TaskDTO.class);
    }

    @Transactional
    public TaskDTO deleteTask(int id){
        Task taskV2 =  repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskDTO taskDTO = mapper.map(taskV2, TaskDTO.class);
        repository.delete(taskV2);
        taskDTO.setDescription(null);
        return taskDTO;
    }

    @Transactional
    public TaskDTO updateTask(Integer id, TaskDTO taskDTO) {
        Task existingTaskV2 = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingTaskV2.setDescription(taskDTO.getDescription());
        existingTaskV2.setTitle(taskDTO.getTitle());
        existingTaskV2.setAssignees(taskDTO.getAssignees());
        existingTaskV2.setStatus(taskDTO.getStatus());
        Task savedTaskV2 = repository.save(existingTaskV2);
        TaskDTO updateTaskDTO = mapper.map(savedTaskV2, TaskDTO.class);
        updateTaskDTO.setDescription(null);
        return updateTaskDTO;
    }
}

