package com.itbangmodkradankanbanapi.db2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @Column(name = "oid")
    private String oid;

    @NotBlank
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @NotBlank
    @Column( name = "name")
    private String name;

    @Column( name = "email")
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
