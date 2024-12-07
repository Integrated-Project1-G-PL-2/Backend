package com.itbangmodkradankanbanapi.db2.controller;

import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db2.config.MicrosoftOAuthConfig;
import com.itbangmodkradankanbanapi.db2.dto.JwtResponse;
import com.itbangmodkradankanbanapi.db2.services.JwtTokenUtil;
import com.itbangmodkradankanbanapi.db2.services.MicrosoftAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@RestController
public class MicrosoftOAuthController {

    @Autowired
    private MicrosoftOAuthConfig oAuthConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MicrosoftAuthService microsoftAuthService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @GetMapping("/login/microsoft")
    public void redirectToMicrosoft(HttpServletResponse response) throws IOException {
        String authorizationUrl = String.format(
                "%s?client_id=%s&response_type=code&redirect_uri=%s&response_mode=query&scope=%s",
                oAuthConfig.getAuthorizationEndpoint(),
                oAuthConfig.getClientId(),
                URLEncoder.encode(oAuthConfig.getRedirectUri(), StandardCharsets.UTF_8),
                URLEncoder.encode(oAuthConfig.getScope(), StandardCharsets.UTF_8)
        );
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback/login")
    public void handleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", oAuthConfig.getClientId());
        body.add("client_secret", oAuthConfig.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", oAuthConfig.getRedirectUri());
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                oAuthConfig.getTokenEndpoint(), HttpMethod.POST, request, Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to exchange code for token");
        }

        Map<String, Object> tokens = tokenResponse.getBody();
        LocalUser localUser = microsoftAuthService.handleMicrosoftToken(tokens);
        String token = jwtTokenUtil.generateTokenFromMicrosoft(localUser);
        String refreshToken = jwtTokenUtil.generateRefreshTokenFromMicrosoft(localUser);

        Cookie accessTokenCookie = new Cookie("access_token", token);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800);
        response.addCookie(refreshTokenCookie);

        response.sendRedirect(oAuthConfig.getFrontSite() + "/callback/login");

    }


    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> handleFavicon() {
        return ResponseEntity.noContent().build();
    }
}