package com.itbangmodkradankanbanapi.db2.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itbangmodkradankanbanapi.db1.v3.entities.LocalUser;
import com.itbangmodkradankanbanapi.db1.v3.service.UserLocalService;
import com.itbangmodkradankanbanapi.db2.config.MicrosoftOAuthConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import java.net.URL;
import java.util.Map;

@Service
public class MicrosoftAuthService {

    private MicrosoftOAuthConfig oAuthConfig;
    private String issuer;
    private JwtUserDetailsService userDetailsService;
    private UserLocalService userLocalService;
    private ModelMapper modelMapper;

    @Autowired
    public MicrosoftAuthService(MicrosoftOAuthConfig oAuthConfig, JwtUserDetailsService jwtUserDetailsService, UserLocalService userLocalService, ModelMapper mapper) {
        this.oAuthConfig = oAuthConfig;
        this.userDetailsService = jwtUserDetailsService;
        this.userLocalService = userLocalService;
        this.modelMapper = mapper;
        issuer = "https://login.microsoftonline.com/" + oAuthConfig.getTenantId() + "/v2.0";
    }


    public boolean isMicrosoftIssuer(String idToken) {
        DecodedJWT jwt = JWT.decode(idToken);
        return jwt.getIssuer().equals(issuer);
    }

    public String getOidFromAccessToken(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String oid = jwt.getClaim("oid").asString();
        if (oid == null) {
            throw new IllegalStateException("OID claim is missing in the token");
        }
        return oid;
    }

    public boolean validateAccessToken(String token) throws Exception {
        DecodedJWT decodedJWT = JWT.decode(token);

        URL jwksUrl = new URL("https://login.microsoftonline.com/" + oAuthConfig.getTenantId() + "/discovery/v2.0/keys");
        JWKSet jwkSet = JWKSet.load(jwksUrl);

        String kid = decodedJWT.getKeyId();
        RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(kid);

        if (rsaKey == null) {
            throw new IllegalArgumentException("No matching key found in JWKS");
        }
        JWSObject jwsObject = JWSObject.parse(token);
        RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
        if (!jwsObject.verify(verifier)) {
            throw new IllegalArgumentException("Invalid token signature");
        }

        Map<String, Object> claims = jwsObject.getPayload().toJSONObject();
        if (!issuer.equals(claims.get("iss"))) {
            throw new IllegalArgumentException("Invalid issuer");
        }

        if (!oAuthConfig.getClientId().equals(claims.get("aud"))) {
            throw new IllegalArgumentException("Invalid audience");
        }

        long exp = (long) claims.get("exp");
        if (System.currentTimeMillis() / 1000 > exp) {
            throw new IllegalArgumentException("Token is expired");
        }
        return true;
    }

    public LocalUser handleMicrosoftToken(Map<String, Object> token) {
        String accessToken = (String) token.get("access_token");
        DecodedJWT jwt = JWT.decode(accessToken);
        String oid = jwt.getClaim("oid").asString();
        String email = jwt.getClaim("upn").asString();
        String name = jwt.getClaim("name").asString();
        String username = jwt.getClaim("given_name").asString();
        System.out.println(oid);
        if (!userDetailsService.existUser(oid, email)) {
            LocalUser newLocaluser = LocalUser.builder()
                    .oid(oid)
                    .email(email)
                    .name(name)
                    .username(username).build();
            return userLocalService.addLocalUser(newLocaluser);
        }
        return modelMapper.map(userDetailsService.getUserByOidAndEmail(oid, email), LocalUser.class);
    }


}
