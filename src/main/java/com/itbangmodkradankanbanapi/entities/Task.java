package com.itbangmodkradankanbanapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.time.ZoneOffset;
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

    @Column(nullable = false, length = 100)
    private String taskTitle;

    @Column(length = 500)
    private String taskDescription;

    @Column(length = 30)
    private String taskAssignees;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TaskStatus taskStatus ;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @CreationTimestamp
    private Timestamp createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @UpdateTimestamp
    private Timestamp updatedOn;

    public enum TaskStatus {
        no_status,
        to_do,
        doing,
        done
    }

}
