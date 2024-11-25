package com.itbangmodkradankanbanapi.db1.v3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilesData {
    @Id
    private String id;
    private String name;
    private String type;
    private String path;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tasks_id", referencedColumnName = "id")
    private Task task;
}
