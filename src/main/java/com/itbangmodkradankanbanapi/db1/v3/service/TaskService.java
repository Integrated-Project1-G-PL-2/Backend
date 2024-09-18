package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.Status;
import com.itbangmodkradankanbanapi.db1.v3.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.db1.v3.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.TaskRepository;
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
    @Autowired
    private StatusService statusService;

    public Task findTaskById(String boardId, int id) throws ItemNotFoundException {
        return repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundException("Task " + id + " does not exist !!!"));
    }

    public List<Task> findAllTask(List<String> statusNames, String sortBy, String boardId) {
        List<Task> taskV2List;
        if (statusNames == null || statusNames.isEmpty()) {
            taskV2List = repository.findAllTaskByBoardId(boardId);
        } else {
            taskV2List = repository.findAllByStatusNamesSorted(statusNames, sortBy, boardId);
        }
        return taskV2List;
    }

    @Transactional
    public TaskDTO createNewTask(TaskDTOForAdd newTask, Board board) {
        Status statusObj = null;
        if (newTask.getStatus() != null) {
            statusObj = statusRepository.findByBoard_IdAndId(board.getId(), newTask.getStatus()).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("Status NOT FOUND"));
        } else {
            statusObj = statusRepository.findByNameAndBoardIsNull("No Status").orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("Status NOT FOUND"));
        }
        Task taskV2 = mapper.map(newTask, Task.class);
        taskV2.setStatus(statusObj);
        taskV2.setBoard(board);
        Task savedTaskV2 = repository.saveAndFlush(taskV2);
        return mapper.map(savedTaskV2, TaskDTO.class);
    }

    @Transactional
    public TaskDTO deleteTask(String boardId, int id) {
        Task taskV2 = repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskDTO taskDTO = mapper.map(taskV2, TaskDTO.class);
        repository.delete(taskV2);
        return taskDTO;
    }

    @Transactional
    public TaskDTO updateTask(Board board, Integer id, TaskDTO taskDTO) {
        Task existingTaskV2 = repository.findByBoard_IdAndId(board.getId(), id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        List<Status> allPossibleStatus = statusService.findAllStatus(board);
        for (Status status : allPossibleStatus) {
            if (status.getId().equals(taskDTO.getStatus().getId())) {
                existingTaskV2.setStatus(status);
            }
        }
        existingTaskV2.setDescription(taskDTO.getDescription());
        existingTaskV2.setTitle(taskDTO.getTitle());
        existingTaskV2.setAssignees(taskDTO.getAssignees());
        Task savedTaskV2 = repository.save(existingTaskV2);
        TaskDTO updateTaskDTO = mapper.map(savedTaskV2, TaskDTO.class);
        updateTaskDTO.setDescription(null);
        return updateTaskDTO;
    }
}

