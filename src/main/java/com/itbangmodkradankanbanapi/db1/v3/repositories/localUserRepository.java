package com.itbangmodkradankanbanapi.db1.v3.repositories;

import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface localUserRepository extends JpaRepository<LocalUser, String> {
    LocalUser findByOid(String oid);
}
