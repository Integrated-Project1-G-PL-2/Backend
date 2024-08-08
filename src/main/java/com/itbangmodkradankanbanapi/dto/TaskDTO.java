package com.itbangmodkradankanbanapi.dto;

import com.itbangmodkradankanbanapi.entities.Status;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTO {
    private Integer id;
    @Size(max = 100, min = 1)
    private String title;
    @Size(max = 500, min = 1)
    private String description;
    @Size(max = 30, min = 1)
    private String assignees;
    private Status status;


    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setAssignees(String assignees) {
        this.assignees = assignees == null ? null : assignees.trim();
    }

}