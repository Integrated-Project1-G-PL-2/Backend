package com.itbangmodkradankanbanapi.db2.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.repositories.BoardRepository;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.exception.ErrorResponse;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        String boardId = extractBoardIdFromPath(request.getServletPath());
        if (boardId != null) {
            request.setAttribute("boardId", boardId);
        }

        String oid = null;
        String jwtToken = null;
        // token ถูก
        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    oid = jwtTokenUtil.getOidFromToken(jwtToken);
                } catch (SecurityException | MalformedJwtException e) {
                    request.setAttribute("Error-Message", "Invalid JWT token");
                } catch (ExpiredJwtException e) {
                    request.setAttribute("Error-Message", "Expired JWT token");
                } catch (UnsupportedJwtException e) {
                    request.setAttribute("Error-Message", "Unsupported JWT token");
                } catch (IllegalArgumentException e) {
                    request.setAttribute("Error-Message", "Missing JWT token");
                } catch (Exception e) {
                    request.setAttribute("Error-Message", "Authentication failure");
                }
            } else {
                request.setAttribute("Error-Message", "Token not starting with Bearer");
            }
        }


        if (oid != null) {
            User user = userRepository.findByOid(oid);
            if (user == null) {
                request.setAttribute("Error-Message", "User not found");
            } else {
                String username = user.getUsername();
                try {
                    if (request.getServletPath().equals("/token")) {
                        chain.doFilter(request, response);
                        return;
                    }
                    UserDetails userDetails = null;
                    if (request.getServletPath().matches("^/v3/boards/[^/]+/invitation$")) {
                        userDetails = this.jwtUserDetailsService.loadUserByUsernameForInvitation(username, jwtToken, boardId);
                    } else {
                        userDetails = this.jwtUserDetailsService.loadUserByUsername(username, jwtToken, boardId);
                    }
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        // token valid and userDetails not null (owner)
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (ItemNotFoundException ex) {
                    writeErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                status.value(),
                status.getReasonPhrase(),
                message,
                null,
                null
        );
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }


    private String extractBoardIdFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 3 && "boards".equals(parts[2])) {
            return parts[3];
        }
        return null;
    }


}

