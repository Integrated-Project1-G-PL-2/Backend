package com.itbangmodkradankanbanapi.v1.service;

import com.itbangmodkradankanbanapi.v1.dto.TaskDTO;
import com.itbangmodkradankanbanapi.v1.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.v1.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private  TaskRepository repository;
    @Autowired
    private ModelMapper mapper;
    public Task findTaskById(int id) throws ItemNotFoundException {
        Task task =  repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Task "+ id +" does not exist !!!" ));
        return task;
    }

    public List<Task> findAllTask() {
        List<Task> tasks = repository.findAllByOrderByCreatedOnAsc();
        return tasks.stream()
                .peek(task -> task.setDescription(null))
                .collect(Collectors.toList());
    }
    @Transactional
    public TaskDTO createNewTask(TaskDTO newTask) throws DataAccessException {
        try {
            Task task = mapper.map(newTask, Task.class);
            Task savedTask = repository.saveAndFlush(task);
            return mapper.map(savedTask, TaskDTO.class);
        } catch (DataAccessException ex) {
            String errorMessage = "Failed to create new task: " + ex.getMessage();
            throw new DataAccessException(errorMessage, ex.getCause()){};
        }
    }

    @Transactional
    public TaskDTO deleteTask(int id){
        Task task =  repository.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskDTO taskDTO = mapper.map(task, TaskDTO.class);
        repository.delete(task);
        taskDTO.setDescription(null);
        return taskDTO;
    }

    @Transactional
    public TaskDTO updateTask(Integer id, TaskDTO taskDTO) {
        Task existingTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setAssignees(taskDTO.getAssignees());
        existingTask.setStatus(taskDTO.getStatus());
        Task savedTask = repository.save(existingTask);
        TaskDTO updateTaskDTO = mapper.map(savedTask, TaskDTO.class);
        updateTaskDTO.setDescription(null);
        return  updateTaskDTO;
    }
}