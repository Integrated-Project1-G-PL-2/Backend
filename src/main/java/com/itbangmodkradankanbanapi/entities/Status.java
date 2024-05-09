package com.itbangmodkradankanbanapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 50 , name = "statusName")
    private String name;

    @Column(length = 200 , name = "statusDescription")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV2> taskV2s ;

}
