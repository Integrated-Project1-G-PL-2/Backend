package com.itbangmodkradankanbanapi.db1.v3.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "center_status")
public class CenterStatus {
    @Id
    @Column(name = "status_id")
    private int id;

    @Column(name = "enable")
    private boolean enable;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;
}
