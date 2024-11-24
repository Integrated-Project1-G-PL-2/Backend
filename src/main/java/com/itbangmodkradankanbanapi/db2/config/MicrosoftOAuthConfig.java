package com.itbangmodkradankanbanapi.db2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "microsoft.oauth")
public class MicrosoftOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String grantType;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    private String meEndpoint;
    private String tenantId;

    public static MicrosoftOAuthConfig getInstance() {
        return new MicrosoftOAuthConfig();
    }
}


