package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.dto.TaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.repositories.TaskRepository;
import com.itbangmodkradankanbanapi.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskV2Service {
    @Autowired
    private TaskV2Repository repository;
    @Autowired
    private ModelMapper mapper;


    public List<TaskV2> findAllTask() {
      return repository.findAll();
    }

}

