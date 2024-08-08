package com.itbangmodkradankanbanapi.v2.repositories;

import com.itbangmodkradankanbanapi.v2.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status , Integer> {
    List<Status> findAllByNameIgnoreCase(String name);
}
