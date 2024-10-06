package com.itbangmodkradankanbanapi.db1.v3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollabDTORequest {

    @Size(max = 50)
    @NotBlank
    String email;
    @Pattern(regexp = "WRITE|READ", message = "Access_right must be either 'WRITE' or 'READ'")
    String access_right;
}
