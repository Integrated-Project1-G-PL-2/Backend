package com.itbangmodkradankanbanapi.db2.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String access_token;
    private String refresh_token;
    public JwtResponse(String token ,String refresh_token) {
        this.access_token = token;
        this.refresh_token = refresh_token;
    }
    public JwtResponse(String token) {
        this.access_token = token;
    }
}
