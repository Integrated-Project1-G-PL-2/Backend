package com.itbangmodkradankanbanapi.db1.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTOForAdd {
    private Integer id;
    @Size(max = 100, min = 1)
    private String title;
    @Size(max = 500, min = 1)
    private String description;
    @Size(max = 30 , min = 1)
    private String assignees;
    private Integer status;


    public void setDescription(String description) {
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public void setTitle(String title) {
        this.title = title == null  ? null : title.trim();
    }

    public void setAssignees(String assignees) {
        this.assignees = assignees == null || assignees.isBlank() ? null : assignees.trim();
    }


}