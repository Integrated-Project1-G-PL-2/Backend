package com.itbangmodkradankanbanapi.db2.controller;

import com.itbangmodkradankanbanapi.db2.config.MicrosoftOAuthConfig;
import com.itbangmodkradankanbanapi.db2.dto.MicrosoftLogin;
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

    @PostMapping("/callback/login")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestBody MicrosoftLogin microsoftLogin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", oAuthConfig.getClientId());
        body.add("client_secret", oAuthConfig.getClientSecret());
        body.add("code", microsoftLogin.getCode());
        body.add("redirect_uri", oAuthConfig.getRedirectUri());
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                oAuthConfig.getTokenEndpoint(), HttpMethod.POST, request, Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to exchange code for token");
        }

        Map<String, Object> tokens = tokenResponse.getBody();
        return ResponseEntity.ok(tokens);
    }


    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> handleFavicon() {
        return ResponseEntity.noContent().build();
    }
}