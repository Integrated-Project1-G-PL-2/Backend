package com.itbangmodkradankanbanapi.db2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "oid")
    private Integer oid;

    @NotBlank
    @Column(nullable = false, length = 50 , name = "username")
    private String username;

    @Column(length = 100 , name = "password",nullable = false)
    private String password;

    @NotBlank
    @Column(length = 100 , name = "name",nullable = false)
    private String name;

    @Column(length = 50 , name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    public enum UserRole{
        LECTURER,
        STAFF,
        STUDENT
    }

}
