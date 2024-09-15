package com.itbangmodkradankanbanapi.db1.v2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusDTOV2 {

    private Integer id;
    @NotBlank
    private String name;
    private String description;

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}