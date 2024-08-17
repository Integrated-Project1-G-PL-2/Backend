package com.itbangmodkradankanbanapi.db2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JwtRequestUser {
    private String userName;
    private String password;
}