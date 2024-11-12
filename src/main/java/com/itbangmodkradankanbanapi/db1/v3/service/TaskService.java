package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTO;
import com.itbangmodkradankanbanapi.db1.v3.dto.TaskDTOForAdd;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.FilesData;
import com.itbangmodkradankanbanapi.db1.v3.entities.Status;
import com.itbangmodkradankanbanapi.db1.v3.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.db1.v3.repositories.StatusRepository;
import com.itbangmodkradankanbanapi.db1.v3.repositories.TaskRepository;
import io.jsonwebtoken.io.IOException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private FilesDataService filesDataService;

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
            statusObj = statusRepository.findById(newTask.getStatus()).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("Status not found on this board"));
            if (statusObj.getBoard() != null) {
                statusObj = statusRepository.findByBoard_IdAndId(board.getId(), newTask.getStatus()).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("Status not found on this board"));
            }
        } else {
            statusObj = statusRepository.findByNameAndBoardIsNull("No Status").orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("Status not found on this board"));
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
    public TaskDTO updateTask(Board board, Integer id, TaskDTOForAdd taskDTO) {
        Task existingTaskV2 = repository.findByBoard_IdAndId(board.getId(), id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        List<Status> allPossibleStatus = statusService.findAllStatus(board);
        Status getStatus = statusRepository.findById(taskDTO.getStatus()).orElseThrow(() -> new ItemNotFoundException("Status not found"));
        for (Status status : allPossibleStatus) {
            if (status.getId().equals(getStatus.getId())) {
                existingTaskV2.setStatus(status);
            }
        }
        existingTaskV2.setDescription(taskDTO.getDescription());
        existingTaskV2.setTitle(taskDTO.getTitle());
        existingTaskV2.setAssignees(taskDTO.getAssignees());
        Task savedTaskV2 = repository.save(existingTaskV2);
        TaskDTO updateTaskDTO = mapper.map(savedTaskV2, TaskDTO.class);
        return updateTaskDTO;
    }

    @Transactional
    public TaskDTO updateFileInTask(Board board, Integer id, MultipartFile[] files) throws IOException {
        Task existingTask = repository.findByBoard_IdAndId(board.getId(), id)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("Task not found"));


        Set<String> existingFileNames = filesDataService.getAllFilesOfTask(id)
                .stream()
                .map(FilesData::getName)
                .collect(Collectors.toSet());

        Set<String> incomingFileNames = Arrays.stream(files)
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toSet());
        // check amount of file
        int totalFileCount = existingFileNames.size() + incomingFileNames.size();
        if (totalFileCount > 10) {
            throw new IOException("Max file limit exceeded (10 files).");
        }
        // check duplicate
        Set<String> duplicateCheck = new HashSet<>(incomingFileNames);
        duplicateCheck.retainAll(existingFileNames);
        if (!duplicateCheck.isEmpty()) {
            throw new IOException("Duplicate file names detected: " + duplicateCheck);
        }

        try {
            for (MultipartFile file : files) {
                filesDataService.uploadFile(file, existingTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapper.map(existingTask, TaskDTO.class);
    }


}

