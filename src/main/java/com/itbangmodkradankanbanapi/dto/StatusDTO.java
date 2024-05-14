package com.itbangmodkradankanbanapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class StatusDTO {

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
