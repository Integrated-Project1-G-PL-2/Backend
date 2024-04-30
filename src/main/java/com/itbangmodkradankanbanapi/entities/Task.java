package com.itbangmodkradankanbanapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @Column(length = 20, name = "taskStatus")
    private TaskStatus status ;


    @CreationTimestamp
    @Column(name = "createdOn")
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "updatedOn")
    private ZonedDateTime updatedOn;

    public enum TaskStatus {
        NO_STATUS,
        TO_DO,
        DOING,
        DONE
    }

}
