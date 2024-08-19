package com.itbangmodkradankanbanapi.db2.dto;

import lombok.Data;

@Data
public class JwtResponse {
    String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
