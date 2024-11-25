package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.itbangmodkradankanbanapi.db1.v3.entities.FilesData;
import com.itbangmodkradankanbanapi.db1.v3.entities.Status;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

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
    private Set<FilesData> filesDataList;


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