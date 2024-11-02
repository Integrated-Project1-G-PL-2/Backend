package com.itbangmodkradankanbanapi.db1.v3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllCollabDTOResponse {
    List<CollabDTOResponse> collab;
    List<CollabDTOResponse> pending;

}
