package com.itbangmodkradankanbanapi.db1.v3.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalUser {
    @Id
    @Size(max = 36)
    @Column(name = "oid", nullable = false)
    private String oid;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;


}
