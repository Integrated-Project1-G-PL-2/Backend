package com.itbangmodkradankanbanapi.db1.v3.entities;

import io.viascom.nanoid.NanoId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
public class Board {
    @Id
    @Size(max = 10)
    @Column(name = "id", nullable = false)
    private String id;
    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status_default")
    private String defaultStatus;

    @Column(name = "createdOn", insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", insertable = false, updatable = false)
    private ZonedDateTime updatedOn;


    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    public Board(String name, String visibility) {
        this.id = NanoId.generate(10);
        this.name = name;
        this.visibility = Visibility.valueOf(visibility);
        this.defaultStatus = "1111";
    }

    public enum Visibility {
        PRIVATE,
        PUBLIC,
    }

}
