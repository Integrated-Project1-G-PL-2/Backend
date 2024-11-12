package com.itbangmodkradankanbanapi.db1.v3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class FilesData {
    @Id
    private String id;
    private String name;
    private String type;
    private String path;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tasks_id", referencedColumnName = "id")
    private Task task;
}
