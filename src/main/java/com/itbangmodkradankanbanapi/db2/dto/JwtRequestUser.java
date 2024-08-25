package com.itbangmodkradankanbanapi.db2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
public class JwtRequestUser {
    @NotBlank
    @Size(max = 50)
    private String userName;

    @NotBlank
    @Size(max = 14)
    private String password;
}