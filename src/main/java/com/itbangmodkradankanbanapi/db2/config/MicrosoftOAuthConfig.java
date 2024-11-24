package com.itbangmodkradankanbanapi.db2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microsoft.oauth")
@Data
public class MicrosoftOAuthConfig {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String grantType;
    private String tokenEndpoint;
    private String meEndpoint;


}

