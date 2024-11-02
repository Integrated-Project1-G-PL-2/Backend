package com.itbangmodkradankanbanapi.db1.v3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "pending")
@NoArgsConstructor
@Data
public class Invitation {

    @EmbeddedId
    Invitation.PendingId id;

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
    private BoardOfUser.Role role;


    public Invitation(LocalUser localUser, Board board, BoardOfUser.Role role) {
        this.id = new PendingId();
        this.id.boardId = board.getId();
        this.id.localUserId = localUser.getOid();
        this.board = board;
        this.localUser = localUser;
        this.role = role;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PendingId implements Serializable {

        @Column(name = "users_oid")
        String boardId;

        @Column(name = "boards_id")
        String localUserId;


    }
}