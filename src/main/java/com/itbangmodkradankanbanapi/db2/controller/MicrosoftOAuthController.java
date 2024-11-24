package com.itbangmodkradankanbanapi.db2.controller;

import com.itbangmodkradankanbanapi.db2.config.MicrosoftOAuthConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
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

    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
    public ResponseEntity<Void> favicon() {
        System.out.println("Favicon request received");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/login/microsoft")
    public void redirectToMicrosoft(HttpServletResponse response) throws IOException {
        String authorizationUrl = String.format(
                "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?" +
                        "client_id=%s&response_type=code&redirect_uri=%s&response_mode=query&scope=%s",
                oAuthConfig.getClientId(),
                URLEncoder.encode(oAuthConfig.getRedirectUri(), StandardCharsets.UTF_8),
                URLEncoder.encode(oAuthConfig.getScope(), StandardCharsets.UTF_8)
        );

        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback/login")
    public ResponseEntity<String> handleCallback(
            @RequestParam String code
    ) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", oAuthConfig.getClientId());
        body.add("client_secret", oAuthConfig.getClientSecret());
        body.add("code", code); // Code received as query parameter
        body.add("redirect_uri", oAuthConfig.getRedirectUri());
        body.add("grant_type", oAuthConfig.getGrantType());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> tokenResponse;

        try {
            tokenResponse = restTemplate.exchange(
                    oAuthConfig.getTokenEndpoint(), HttpMethod.POST, request, Map.class
            );
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to exchange code for token", e);
        }

        Map<String, Object> tokens = tokenResponse.getBody();
        String accessToken = (String) tokens.get("access_token");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);

        ResponseEntity<Map> userResponse;
        try {
            userResponse = restTemplate.exchange(
                    oAuthConfig.getMeEndpoint(), HttpMethod.GET, userRequest, Map.class
            );
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user details", e);
        }

        Map<String, Object> userInfo = userResponse.getBody();
        System.out.println(userInfo.toString());
        return ResponseEntity.ok("User Info: " + userInfo);
    }
}
