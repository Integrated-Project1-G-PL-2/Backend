package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CollabDTOResponse {
    private String oid;
    private String name;
    private String email;
    @Pattern(regexp = "WRITE|READ", message = "Access_right must be either 'WRITE' or 'READ'")
    private String access_right;
    private ZonedDateTime addedOn;

    public CollabDTOResponse(String oid, @NotBlank @Size(max = 100) String name, @NotBlank @Size(max = 50) String email, BoardOfUser.Role role, ZonedDateTime addedOn) {
        this.name = name;
        this.email = email;
        if (role.equals(BoardOfUser.Role.COLLABORATOR)) {
            this.access_right = "WRITE";
        } else {
            this.access_right = "READ";
        }
        this.addedOn = addedOn;
        this.oid = oid;
    }
}
