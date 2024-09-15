package com.itbangmodkradankanbanapi.db2.repositories;


import com.itbangmodkradankanbanapi.db2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
