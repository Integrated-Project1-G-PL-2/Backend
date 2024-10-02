package com.itbangmodkradankanbanapi.db2.services;

import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("#{${jwt.max-token-interval-hour}*60*60*1000}")
    private long JWT_TOKEN_VALIDITY;
    @Value("${itbkk.app.jwtRefreshCookieName}")
    private String JWT_REFRESH_COOKIE_NAME;


    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getOidFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("oid", String.class));
    }

    public User.UserRole getRoleFromToken(String token) {
        String roleString = getClaimFromToken(token, claims -> claims.get("role", String.class));
        return User.UserRole.valueOf(roleString);
    }


    public ResponseCookie generateRefreshJwtCookie(User user) {
        String refreshToken = generateRefreshToken(user);
        return generateCookie(JWT_REFRESH_COOKIE_NAME, refreshToken, "/token");
    }


    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, JWT_REFRESH_COOKIE_NAME);
    }


    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(JWT_REFRESH_COOKIE_NAME, null).path("/token").build();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userDetails.getName());
        claims.put("oid", userDetails.getOid());
        claims.put("role", userDetails.getRole());
        claims.put("email", userDetails.getEmail());
        return doGenerateToken(claims, userDetails.getUsername());
    }

    public String generateRefreshToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("oid", userDetails.getOid());
        return Jwts.builder().setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .setIssuer("https://intproj23.sit.kmutt.ac.th/pl2/")
                .signWith(signatureAlgorithm, SECRET_KEY).compact();
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setHeaderParam("typ", "JWT").
                setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .setIssuer("https://intproj23.sit.kmutt.ac.th/pl2/")
                .signWith(signatureAlgorithm, SECRET_KEY).compact();
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(24 * 60 * 60)
                .httpOnly(true)
                .sameSite("None")
                .build();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        final String oid = getOidFromToken(token);
        User.UserRole role = getRoleFromToken(token);
        return userRepository.existsByUsernameAndOidAndRole(username, oid, role) && !isTokenExpired(token) && (userDetails != null);
    }

    public Boolean validateRefreshToken(String token) {
        final String oid = getOidFromToken(token);
        return userRepository.existsByOid(oid) && !isTokenExpired(token);
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

}
