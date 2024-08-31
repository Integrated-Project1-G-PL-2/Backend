package com.itbangmodkradankanbanapi.db1.repositories;

import com.itbangmodkradankanbanapi.db1.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,String> {
}
