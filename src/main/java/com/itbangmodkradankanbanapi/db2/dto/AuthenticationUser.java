package com.itbangmodkradankanbanapi.db2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationUser {
    @NotBlank
    @Size(max = 50)
    private String userName;
    @NotBlank
    @Size(max = 14)
    private String password;

}
