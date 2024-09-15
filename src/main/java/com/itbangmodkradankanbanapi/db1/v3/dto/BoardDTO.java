package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardDTO {
    @Size(max = 120)
    @NotBlank
    private String name;

    private String id;

    private LocalUser owner;

}
