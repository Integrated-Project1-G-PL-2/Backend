package com.itbangmodkradankanbanapi.db2.controller;

import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.repositories.LocalUserRepository;
import com.itbangmodkradankanbanapi.db2.dto.JwtRequestUser;
import com.itbangmodkradankanbanapi.db2.dto.JwtResponse;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import com.itbangmodkradankanbanapi.exception.UnauthorizeAccessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController

public class AuthenticationController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;

    @Autowired
    LocalUserRepository LocalUserRepository;
    @Autowired
    private LocalUserRepository localUserRepository;

    /* <FOR HTTP ONLY COOKIE>
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Invalid user or password");
        }
        User user = userRepository.findByUsername(jwtRequestUser.getUserName());
        String token = jwtTokenUtil.generateToken(user);
        ResponseCookie jwtRefreshCookie = jwtTokenUtil.generateRefreshJwtCookie(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new JwtResponse(token));
    }

    @PostMapping("/token")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request) {
        String token = jwtTokenUtil.getJwtRefreshFromCookies(request);
        if (token.isBlank()) {
            throw new UnauthorizeAccessException(HttpStatus.UNAUTHORIZED, "refresh not found");
        }
        if (!jwtTokenUtil.validateRefreshToken(token)) {
            throw new UnauthorizeAccessException(HttpStatus.UNAUTHORIZED, "Invalid refresh-token");
        }
        String oid = jwtTokenUtil.getOidFromToken(token);
        User user = userRepository.findByOid(oid);
        String newToken = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(newToken));
    }
    */

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid JwtRequestUser jwtRequestUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Invalid user or password");
        }
        User user = userRepository.findByUsername(jwtRequestUser.getUserName());
        String token = jwtTokenUtil.generateToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        return ResponseEntity.ok(new JwtResponse(token, refreshToken));
    }

    @PostMapping("/token")
    public ResponseEntity<Object> refreshToken(@RequestHeader("Authorization") String token, @RequestParam(required = false) boolean msal) throws UnauthorizeAccessException {
        try {
            String onlyToken = null;
            if (token.startsWith("Bearer ")) {
                onlyToken = token.substring(7);
            }
            if (!jwtTokenUtil.validateRefreshToken(onlyToken) || onlyToken == null) {
                throw new UnauthorizeAccessException(HttpStatus.UNAUTHORIZED, "Invalid refresh-token");
            }
            String newToken = null;
            if (msal) {
                String oid = jwtTokenUtil.getOidFromToken(onlyToken);
                LocalUser localUser = localUserRepository.findByOid(oid);
                newToken = jwtTokenUtil.generateTokenFromMicrosoft(localUser);
            } else {
                String oid = jwtTokenUtil.getOidFromToken(onlyToken);
                User user = userRepository.findByOid(oid);
                newToken = jwtTokenUtil.generateToken(user);
            }
            return ResponseEntity.ok(new JwtResponse(newToken));
        } catch (Exception e) {
            throw new UnauthorizeAccessException(HttpStatus.UNAUTHORIZED, "Invalid refresh-token");
        }

    }


    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String requestTokenHeader) {
        Claims claims = null;
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "JWT Token does not begin with Bearer String");
        }
        return ResponseEntity.ok(claims);
    }
}
