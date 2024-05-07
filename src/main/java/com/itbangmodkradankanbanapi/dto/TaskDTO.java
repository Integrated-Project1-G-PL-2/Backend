package com.itbangmodkradankanbanapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itbangmodkradankanbanapi.entities.Task;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
        if (description != null ){
            this.description = description.trim();
        }else {
            this.description = description;
        }
    }

}
