package com.itbangmodkradankanbanapi.db2.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import com.itbangmodkradankanbanapi.exception.ErrorResponse;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Component
public class JwtAnonymousAuthFilter extends OncePerRequestFilter {

    @Autowired
    private BoardService boardService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        if (request.getServletPath().equals("/login/microsoft") || request.getServletPath().equals("/callback/login") || request.getServletPath().equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }
        //PUBLIC ACCESS CHECK
        if (HttpMethod.GET.matches(request.getMethod()) && SecurityContextHolder.getContext().getAuthentication() == null && request.getAttribute("Error-Message") == null) {
            List<GrantedAuthority> authorities = new LinkedList<>();
            String boardId = (String) request.getAttribute("boardId");
            try {
                Board board = boardService.getBoardById(boardId);
                if (boardService.isPublicAccessibility(board)) {
                    authorities.add(new SimpleGrantedAuthority("PUBLIC-ACCESS"));
                } else {
                    authorities.add(new SimpleGrantedAuthority("ANONYMOUS"));
                    request.setAttribute("Error-Message", "FORBIDDEN");
                }
            } catch (ItemNotFoundException ex) {
                writeErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage());
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, authorities));
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

}