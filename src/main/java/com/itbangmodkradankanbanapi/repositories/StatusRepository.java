package com.itbangmodkradankanbanapi.repositories;

import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.TaskV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status , Integer> {

}
