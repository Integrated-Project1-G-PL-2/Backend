package com.itbangmodkradankanbanapi.db2.dto;

import lombok.Data;

@Data
public class JwtResponse {
    String access_token;

    public JwtResponse(String token) {
        this.access_token = token;
    }
}