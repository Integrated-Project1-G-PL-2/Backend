package com.itbangmodkradankanbanapi.db2.controller;



import com.itbangmodkradankanbanapi.db2.dto.AuthenticationUser;
import com.itbangmodkradankanbanapi.db2.dto.RequestResponse;
import com.itbangmodkradankanbanapi.db2.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

//    @PostMapping("/signup")
//    public ResponseEntity<RequestResponse> signUp(@RequestBody RequestResponse signUpRequest){
//        return  ResponseEntity.ok(authenticationService.signUp(signUpRequest));
//    }

    @PostMapping("/signin")
    public ResponseEntity<RequestResponse> signIn(@Valid  @RequestBody AuthenticationUser signInRequest){
        return  ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }
//    @PostMapping("/refresh")
//    public ResponseEntity<RequestResponse> refreshToken(@RequestBody RequestResponse refreshTokenRequest){
//        return  ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
//    }

}