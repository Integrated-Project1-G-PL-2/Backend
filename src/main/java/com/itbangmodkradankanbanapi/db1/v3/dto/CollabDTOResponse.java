package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CollabDTOResponse {
    private String boardId;
    private String CollaboratorName;
    private String CollaboratorEmail;
    private String oid;
    private String name;
    private String email;
    @Pattern(regexp = "WRITE|READ", message = "Access_right must be either 'WRITE' or 'READ'")
    private String accessRight;
    private ZonedDateTime addedOn;

    public CollabDTOResponse(String oid, String name, String email, BoardOfUser.Role role, ZonedDateTime addedOn) {
        this.name = name;
        this.email = email;
        if (role.equals(BoardOfUser.Role.WRITE)) {
            this.accessRight = "WRITE";
        } else {
            this.accessRight = "READ";
        }
        this.addedOn = addedOn;
        this.oid = oid;
    }

    public CollabDTOResponse(String boardId, String CollaboratorName, String CollaboratorEmail, ZonedDateTime addedOn) {
        this.boardId = boardId;
        this.CollaboratorName = CollaboratorName;
        this.CollaboratorEmail = CollaboratorEmail;
        this.addedOn = addedOn == null ? ZonedDateTime.now() : addedOn;
    }

    public CollabDTOResponse(String oid, String name, String email, BoardOfUser.Role role) {
        this.name = name;
        this.email = email;
        if (role.equals(BoardOfUser.Role.WRITE)) {
            this.accessRight = "WRITE";
        } else {
            this.accessRight = "READ";
        }
        this.oid = oid;
    }

    public CollabDTOResponse(BoardOfUser.Role role) {
        if (role.equals(BoardOfUser.Role.WRITE)) {
            this.accessRight = "WRITE";
        } else {
            this.accessRight = "READ";
        }
    }
}
