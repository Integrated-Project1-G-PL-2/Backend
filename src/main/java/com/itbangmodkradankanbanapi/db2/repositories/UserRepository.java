package com.itbangmodkradankanbanapi.db2.repositories;


import com.itbangmodkradankanbanapi.db2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findByOid(String oid);

    Boolean existsByUsernameAndOidAndRole(String username, String oid, User.UserRole role);

    Boolean existsByOid(String oid);

    Optional<User> findUserByEmail(String email);
}
