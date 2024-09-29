package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardDTO {
    @Size(max = 120)
    private String name;

    private String id;

    private LocalUserDTO owner;

    @Pattern(regexp = "PUBLIC|PRIVATE", message = "Visibility must be either 'PUBLIC' or 'PRIVATE'")
    private String visibility = "PRIVATE";

    public void setOwner(LocalUser owner) {
        if (owner != null) {
            this.owner = new LocalUserDTO(owner.getName());
        }
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LocalUserDTO {
        private String name;

        public LocalUserDTO(String name) {
            this.name = name;
        }
    }
}
