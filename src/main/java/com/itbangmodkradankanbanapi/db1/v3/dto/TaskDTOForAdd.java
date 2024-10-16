package com.itbangmodkradankanbanapi.db1.v3.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTOForAdd {
    private Integer id;
    @Size(max = 100)
    private String title;
    @Size(max = 500)
    private String description;
    @Size(max = 30)
    private String assignees;
    private Integer status;


    public void setDescription(String description) {
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setAssignees(String assignees) {
        this.assignees = assignees == null || assignees.isBlank() ? null : assignees.trim();
    }


}