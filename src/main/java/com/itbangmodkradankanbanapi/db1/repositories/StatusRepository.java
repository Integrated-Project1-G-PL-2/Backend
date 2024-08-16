package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status , Integer> {
    List<Status> findAllByNameIgnoreCase(String name);

}
