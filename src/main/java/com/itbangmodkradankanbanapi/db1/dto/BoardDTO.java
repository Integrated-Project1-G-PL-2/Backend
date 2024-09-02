package com.itbangmodkradankanbanapi.db1.dto;

import com.itbangmodkradankanbanapi.db1.entities.LocalUser;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardDTO {
    @Size(max = 120)
    private String name;

    private String id;

    private LocalUser owner;

}
