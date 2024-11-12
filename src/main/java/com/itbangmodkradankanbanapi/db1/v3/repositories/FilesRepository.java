package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.FilesData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<FilesData, String> {
    Optional<FilesData> findByNameAndTask_Id(String string, int taskId);

    Boolean existsByTaskIdAndName(int taskId, String name);

    List<FilesData> findByTaskId(int taskId);
}
