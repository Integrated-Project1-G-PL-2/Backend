package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.CenterStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenterStatusRepository extends JpaRepository<CenterStatus, Integer> {
    List<CenterStatus> findAllByOrderByIdAsc();
}
