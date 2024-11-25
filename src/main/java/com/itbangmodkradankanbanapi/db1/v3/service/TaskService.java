package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.dto.FilesDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    private StorageService storageService;

    public Task findTaskById(String boardId, int id) throws ItemNotFoundException {
        return repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundException("Task " + id + " does not exist !!!"));
    }

    public List<Task> findAllTask(List<String> statusNames, String sortBy, String boardId) {
        List<Task> taskV2List;
        if (statusNames == null || statusNames.isEmpty()) {
            taskV2List = repository.findAllByBoard_Id(boardId);
        } else {
            taskV2List = repository.findAllByStatusNamesSorted(statusNames, sortBy, boardId);
        }
        taskV2List = taskV2List.stream()
                .peek(task -> task.setFilesDataList(new HashSet<>(storageService.getAllFile(task))))
                .toList();
        return taskV2List;
    }

    @Transactional
    public TaskDTO createNewTask(TaskDTOForAdd newTask, Board board, MultipartFile[] file) {
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
        Set<FilesData> uploadFileInTask = uploadFileInTask(savedTaskV2, file, false);
        TaskDTO saveTasKDTO = mapper.map(savedTaskV2, TaskDTO.class);
        saveTasKDTO.setFilesDataList(uploadFileInTask);
        return saveTasKDTO;
    }

    @Transactional
    public TaskDTO deleteTask(String boardId, int id) {
        Task taskV2 = repository.findByBoard_IdAndId(boardId, id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskDTO taskDTO = mapper.map(taskV2, TaskDTO.class);
        repository.delete(taskV2);
        return taskDTO;
    }

    @Transactional
    public TaskDTO updateTask(Board board, Integer id, TaskDTOForAdd taskDTO, MultipartFile[] files) {
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
        Set<FilesData> uploadFileInTask = uploadFileInTask(savedTaskV2, files, true);
        TaskDTO updateTaskDTO = mapper.map(savedTaskV2, TaskDTO.class);
        updateTaskDTO.setFilesDataList(uploadFileInTask);
        return updateTaskDTO;
    }

    public TaskDTO deleteFileFormTask(String boardId, int id, FilesDTO filesDTO) {
        Task taskV2 = findTaskById(boardId, id);
        List<FilesData> existFile = null;
        for (String file : filesDTO.getFiles()) {
            existFile = storageService.getAllFile(taskV2).stream().filter(fileData -> {
                String fileName = fileData.getPath().substring(fileData.getPath().lastIndexOf("/") + 1);
                return fileName.equals(file);
            }).collect(Collectors.toList());
        }
        if (existFile != null && existFile.size() == filesDTO.getFiles().length) {
            for (FilesData file : existFile) {
                storageService.deleteFile(file);
            }
        }
        taskV2.setFilesDataList(new HashSet<>(storageService.getAllFile(taskV2)));
        return mapper.map(taskV2, TaskDTO.class);
    }

    public TaskDTO deleteFileFormTask1(String boardId, int id, String fileName) {
        Task taskV2 = findTaskById(boardId, id);
        FilesData existFile = storageService.getAllFile(taskV2).stream().filter(fileData -> fileData.getPath().substring(fileData.getPath().lastIndexOf("/") + 1).equals(fileName)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File doesn't exist"));
        storageService.deleteFile(existFile);
        taskV2.setFilesDataList(new HashSet<>(storageService.getAllFile(taskV2)));
        return mapper.map(taskV2, TaskDTO.class);
    }

    private Set<FilesData> uploadFileInTask(Task task, MultipartFile[] files, Boolean isEditing) {
        if (files != null) {
            Set<FilesData> resultFileData = new HashSet<>();
            int amountOfFile = storageService.countFilesInTask(task);
            System.out.println("amount of file = " + amountOfFile);
            if (files.length + amountOfFile > 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum files in one task is 10");
            }
            List<String> existFile = Arrays.stream(files).map(MultipartFile::getOriginalFilename).filter(originalFilename -> {
                return storageService.isExistFile(task, originalFilename);
            }).toList();
            if (existFile.isEmpty()) {
                for (MultipartFile file : files) {
                    resultFileData.add(storageService.uploadFile(file, task));
                }
                return resultFileData;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "File name " + existFile + " already exist");
            }

        }
        return null;
    }


}

