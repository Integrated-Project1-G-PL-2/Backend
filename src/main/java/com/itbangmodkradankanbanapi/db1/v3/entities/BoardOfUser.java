package com.itbangmodkradankanbanapi.db1.v3.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "board_of_user")
@NoArgsConstructor
public class BoardOfUser {
    @EmbeddedId
    BoardUserKey id;

    @ManyToOne
    @MapsId("boardId")
    @JoinColumn(name = "boards_id")
    Board board;

    @ManyToOne
    @MapsId("localUserId")
    @JoinColumn(name = "users_oid")
    LocalUser localUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "addedOn", insertable = false, updatable = false)
    private ZonedDateTime addedOn;

    public BoardOfUser(LocalUser localUser, Board board, Role role) {
        this.id = new BoardUserKey();
        this.id.boardId = board.getId();
        this.id.localUserId = localUser.getOid();
        this.board = board;
        this.localUser = localUser;
        this.role = role;
    }

    @Embeddable
    @Data
    public static class BoardUserKey implements Serializable {

        @Column(name = "users_oid")
        String boardId;

        @Column(name = "boards_id")
        String localUserId;


    }

    public enum Role {
        OWNER,
        VISITOR,
        COLLABORATOR,
    }
}
