package com.itbangmodkradankanbanapi.db1.v2.dto;

import com.itbangmodkradankanbanapi.db1.v2.entities.StatusV2;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTOV2 {
    private Integer id;
    @Size(max = 100, min = 1)
    private String title;
    @Size(max = 500, min = 1)
    private String description;
    @Size(max = 30, min = 1)
    private String assignees;
    private StatusV2 status;


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