package com.itbangmodkradankanbanapi.db2.services;


import com.itbangmodkradankanbanapi.db2.dto.RequestResponse;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private  JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public RequestResponse signUp(RequestResponse register){
        RequestResponse requestResponse = new RequestResponse();
        try {
            User user = new User();
            user.setEmail(register.getEmail());
            user.setPassword(passwordEncoder.encode(register.getPassword()));
            user.setRole(register.getRole());
            User userResult = repository.save(user);
            if(userResult != null && userResult.getId() > 0){
                requestResponse.setUsers(userResult);
                requestResponse.setMessage("User Saved Successfully");
                requestResponse.setStatusCode(200);
            }

        }catch (Exception e){
            requestResponse.setStatusCode(500);
            requestResponse.setError(e.getMessage());
        }
        return requestResponse;
    }

    public RequestResponse signIn(RequestResponse signIn){
        RequestResponse requestResponse = new RequestResponse();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signIn.getEmail(),signIn.getPassword()));
            var user = repository.findByEmail(signIn.getEmail()).orElseThrow();
            System.out.println("USER IS " + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(),user);
            requestResponse.setStatusCode(200);
            requestResponse.setToken(jwt);
            requestResponse.setRefreshToken(refreshToken);
            requestResponse.setExpirationTime("24Hr");
            requestResponse.setMessage("Succeed Signed In");
        }catch (Exception e){
            requestResponse.setStatusCode(500);
            requestResponse.setError(e.getMessage());
        }
        return requestResponse;

    }
    public  RequestResponse refreshToken(RequestResponse refreshToken){
        RequestResponse requestResponse = new RequestResponse();
        String email = jwtUtils.extractUsername(refreshToken.getToken());
        User user = repository.findByEmail(email).orElseThrow();
        if(jwtUtils.isTokenValid(refreshToken.getToken(),user)){
            var jwt = jwtUtils.generateToken(user);
            requestResponse.setStatusCode(200);
            requestResponse.setToken(jwt);
            requestResponse.setRefreshToken(refreshToken.getToken());
            requestResponse.setExpirationTime("24Hr");
            requestResponse.setMessage("Successfully Refreshed Token");
        }
        requestResponse.setStatusCode(500);
        return  requestResponse;
    }

}