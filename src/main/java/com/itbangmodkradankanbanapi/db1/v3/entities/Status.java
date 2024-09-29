package com.itbangmodkradankanbanapi.db1.v3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 50, name = "statusName")
    private String name;

    @Column(length = 200, name = "statusDescription")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<Task> taskV2s;

    @Column(name = "createdOn", insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", insertable = false, updatable = false)
    private ZonedDateTime updatedOn;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "boardId", referencedColumnName = "id")
    private Board board;
}
