package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.v3.entities.FilesData;
import com.itbangmodkradankanbanapi.db1.v3.entities.Task;
import com.itbangmodkradankanbanapi.db1.v3.repositories.FilesRepository;
import io.viascom.nanoid.NanoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class FilesDataService {
    private static final String STORAGE_FOLDER = "uploads/";

    @Autowired
    private FilesRepository fileDataRepository;

    public FilesDataService(FilesRepository fileDataRepository) throws IOException {
        this.fileDataRepository = fileDataRepository;
        Path storagePath = Paths.get(STORAGE_FOLDER);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
    }

    public String uploadFile(MultipartFile file, Task task) throws IOException {
        String fileName = file.getOriginalFilename();
        String taskFolderPath = STORAGE_FOLDER + task.getId();
        
        Files.createDirectories(Paths.get(taskFolderPath));

        String fullFilePath = taskFolderPath + "/" + fileName;

        FilesData fileData = FilesData.builder()
                .id(NanoId.generate(10))
                .name(fileName)
                .type(file.getContentType())
                .path(fullFilePath)
                .task(task)
                .build();
        fileDataRepository.save(fileData);

        file.transferTo(new File(fullFilePath));

        return "File uploaded successfully: " + fullFilePath;
    }

    public byte[] downloadFile(String fileName, Task task) throws IOException {
        Optional<FilesData> fileDataOptional = fileDataRepository.findByNameAndTask_Id(fileName, task.getId());
        if (fileDataOptional.isPresent()) {
            Path filePath = Paths.get(fileDataOptional.get().getPath());
            return Files.readAllBytes(filePath);
        } else {
            throw new IOException("File not found: " + fileName);
        }
    }

    public List<FilesData> getAllFilesOfTask(int taskId) {
        return fileDataRepository.findByTaskId(taskId);
    }
}
