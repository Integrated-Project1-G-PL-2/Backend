package com.itbangmodkradankanbanapi.db1.v2.repositories;

import com.itbangmodkradankanbanapi.db1.v2.entities.StatusV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepositoryV2 extends JpaRepository<StatusV2, Integer> {
    List<StatusV2> findAllByNameIgnoreCase(String name);

}
