package com.itbangmodkradankanbanapi.db2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Size(max = 36)
    @Column(name = "oid", nullable = false)
    private String oid;

    @NotBlank
    @Size(max = 50)
    @Column(name = "username",nullable = false)
    private String username;

    @Size(max = 100)
    @Column(name = "password",nullable = false)
    private String password;

    @NotBlank
    @Size(max = 100)
    @Column( name = "name",nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column( name = "email",nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private UserRole role;

    public enum UserRole{
        LECTURER,
        STAFF,
        STUDENT
    }

}
