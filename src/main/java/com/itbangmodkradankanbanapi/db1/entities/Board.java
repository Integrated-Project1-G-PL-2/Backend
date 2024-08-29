package com.itbangmodkradankanbanapi.db1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @Size(max = 36)
    @Column(name = "id", nullable = false)
    private String id;
    @NotBlank
    @Size(max = 120)
    @Column(name = "name",nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "users_oid", referencedColumnName = "oid")
    private LocalUser localUser;

}
