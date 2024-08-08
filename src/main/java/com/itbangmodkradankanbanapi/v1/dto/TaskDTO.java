package com.itbangmodkradankanbanapi.v1.dto;

import com.itbangmodkradankanbanapi.v1.entities.Task;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTO {
    private Integer id;
    @Size(max = 100, min = 1)
    private String title;
    @Size(max = 500, min = 1)
    private String description;
    @Size(max = 30 , min = 1)
    private String assignees;
    private Task.TaskStatus status ;


    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setTitle(String title) {
     this.title = title == null ? null : title.trim();
    }

    public void setAssignees(String assignees) {
        this.assignees = assignees == null ? null : assignees.trim();
    }
    public void setStatus(Task.TaskStatus status) {
        this.status = status == null ? Task.TaskStatus.NO_STATUS : status;
    }
}
