package com.itbangmodkradankanbanapi.db1.v3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false, length = 100, name = "taskTitle")
    private String title;

    @Column(length = 500, name = "taskDescription")
    private String description;

    @Column(length = 30, name = "taskAssignees")
    private String assignees;

    @ManyToOne
    @JoinColumn(name = "taskStatus")
    private Status status;

    @Column(name = "createdOn", insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", insertable = false, updatable = false)
    private ZonedDateTime updatedOn;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "boardId", referencedColumnName = "id")
    private Board board;
}
