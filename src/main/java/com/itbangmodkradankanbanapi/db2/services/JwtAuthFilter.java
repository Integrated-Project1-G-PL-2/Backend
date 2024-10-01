package com.itbangmodkradankanbanapi.db2.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import com.itbangmodkradankanbanapi.exception.ErrorResponse;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardService boardService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String oid = null;
        if (request.getServletPath().equals("/login")) {
            chain.doFilter(request, response);
            return;
        } else if (request.getServletPath().matches("/v3/boards/[\\w-]+.*") && request.getMethod().equals("GET")) {
            Board board = null;
            try {
                String boardId = extractBoardIdFromPath(request.getServletPath());
                board = boardService.getBoardById(boardId);
            } catch (ItemNotFoundException ex) {
                writeErrorResponse(response, HttpStatus.NOT_FOUND, "Board not found");
            }
            if ("PUBLIC".equals(board.getVisibility().toString())) {
                chain.doFilter(request, response);
                return;
            } else if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                writeErrorResponse(response, HttpStatus.FORBIDDEN, "Invalid token");
                return;
            }
        }
        /* <FOR HTTP ONLY COOKIE>
        else if (request.getServletPath().equals("/token")) {
            chain.doFilter(request, response);
            return;
        }*/
        try {
            if (requestTokenHeader != null) {
                if (requestTokenHeader.startsWith("Bearer ")) {
                    jwtToken = requestTokenHeader.substring(7);
                    if (jwtToken.split("\\.").length != 3) {
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Not well-formed JWT token");
                        return;
                    }
                    try {
                        oid = jwtTokenUtil.getOidFromToken(jwtToken);

                    } catch (SignatureException e) {
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT signature");
                        return;
                    } catch (ExpiredJwtException e) {
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Expired JWT token");
                        return;
                    }
                } else {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT Token does not begin with Bearer String");
                    return;
                }
            } else {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT Token not found");
                return;
            }
            if (oid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByOid(oid);
                if (user == null) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "User oid does not exitst!!");
                    return;
                }
                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(user.getUsername());
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            chain.doFilter(request, response);
        } catch (Exception ex) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Jwt error");
        }
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
        if (parts.length > 2 && "boards".equals(parts[2])) {
            return parts[3];
        }
        return null;
    }


}

