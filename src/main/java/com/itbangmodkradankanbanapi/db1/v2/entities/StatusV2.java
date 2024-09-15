package com.itbangmodkradankanbanapi.db1.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "statusv2")
public class StatusV2 {
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
    private List<TaskV2> taskV2s;

}
