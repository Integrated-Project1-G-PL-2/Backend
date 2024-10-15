package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.itbangmodkradankanbanapi.db1.v3.entities.BoardOfUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class AllBoardDTOResponse {
    private List<BoardOfUser> collab;
    private List<BoardOfUser> personal;

}
