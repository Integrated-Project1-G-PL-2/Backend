package com.itbangmodkradankanbanapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itbangmodkradankanbanapi.entities.Task;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.Optional;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {
    private Integer id;
    @Size(max = 100)
    private String title;
    @Size(max = 500)
    private String description;
    @Size(max = 30)
    private String assignees;
    private Task.TaskStatus status ;
}
