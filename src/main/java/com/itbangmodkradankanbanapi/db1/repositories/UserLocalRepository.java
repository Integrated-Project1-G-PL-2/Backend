package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocalRepository extends JpaRepository<LocalUser,String> {
    LocalUser findByOid(String oid);
}
