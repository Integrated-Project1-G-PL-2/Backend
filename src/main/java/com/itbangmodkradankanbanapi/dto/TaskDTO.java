package com.itbangmodkradankanbanapi.dto;

import com.itbangmodkradankanbanapi.entities.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Integer id;
    private String taskTitle;
    private String taskAssignees;
    private Task.TaskStatus taskStatus ;
}
