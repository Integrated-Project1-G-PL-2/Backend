package com.itbangmodkradankanbanapi.db1.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "board_of_user")
public class BoardOfUser {
    @EmbeddedId
    BoardUserKey id;

    @ManyToOne
    @MapsId("boardId")
    @JoinColumn(name = "users_oid")
    Board board;

    @ManyToOne
    @MapsId("localUserId")
    @JoinColumn(name = "boards_id")
    LocalUser localUser;

    private String role;

    @Embeddable
    @Data
    public class BoardUserKey implements Serializable {

        @Column(name = "users_oid")
        String boardId;

        @Column(name = "boards_id")
        String localUserId;
    }
}
