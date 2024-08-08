package com.itbangmodkradankanbanapi.v1.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100 , name = "taskTitle")
    private String title;

    @Column(length = 500, name = "taskDescription")
    private String description;

    @Column(length = 30 , name = "taskAssignees")
    private String assignees;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "taskStatus" )
    private TaskStatus status ;

    @Column(name = "createdOn" , insertable = false , updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", insertable = false , updatable = false)
    private ZonedDateTime updatedOn;

    public enum TaskStatus {
        NO_STATUS,
        TO_DO,
        DOING,
        DONE
    }

}
