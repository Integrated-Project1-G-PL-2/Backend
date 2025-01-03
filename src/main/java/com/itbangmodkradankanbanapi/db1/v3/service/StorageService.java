package com.itbangmodkradankanbanapi.db1.v3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.itbangmodkradankanbanapi.db1.v3.entities.FilesData;
import com.itbangmodkradankanbanapi.db1.v3.entities.Task;
import com.itbangmodkradankanbanapi.db1.v3.repositories.FilesRepository;
import io.viascom.nanoid.NanoId;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private FilesRepository filesRepository;

    @Transactional
    public FilesData uploadFile(MultipartFile file, Task task) {
        File fileObj = convertMultiPartFileToFile(file);
        String id = NanoId.generate(10);
        String fileName = id + "_" + file.getOriginalFilename();

        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(fileObj);

            if (mimeType.equals("text/plain")) {
                mimeType = mimeType + "; charset=utf-8";
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(mimeType);
            metadata.setContentLength(file.getSize());


            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, encodedFileName, fileObj);
            putObjectRequest.setMetadata(metadata);


            s3Client.putObject(putObjectRequest);

            String fileUrl = s3Client.getUrl(bucketName, encodedFileName).toString();


            fileObj.delete();

            String fileExtension = getFileExtension(file.getOriginalFilename());
            FilesData fileData = FilesData.builder()
                    .id(id)
                    .name(getFileNameWithoutExtension(file.getOriginalFilename()))
                    .path(fileUrl)
                    .type(fileExtension)
                    .task(task)
                    .build();

            return filesRepository.save(fileData);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File download failed");
        }
    }

    @Transactional
    public void deleteFile(FilesData fileData) {
        String fileUrl = fileData.getPath();
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        s3Client.deleteObject(bucketName, fileName);
        filesRepository.delete(fileData);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error converting multipart file to file: " + e.getMessage());
        }
        return convertedFile;
    }


    public int countFilesInTask(Task task) {
        return filesRepository.countByTaskId(task.getId());
    }

    public Boolean isExistFile(Task task, String fileName) {
        System.out.println(getFileNameWithoutExtension(fileName));
        System.out.println(getFileExtension(fileName));
        return filesRepository.existsByTask_IdAndNameAndType(task.getId(), getFileNameWithoutExtension(fileName), getFileExtension(fileName));
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    public List<FilesData> getAllFile(Task task) {
        return filesRepository.findAllByTask_Id(task.getId());
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }


}
