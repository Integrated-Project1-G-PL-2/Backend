package com.itbangmodkradankanbanapi.db2.services;

import com.itbangmodkradankanbanapi.db1.v3.entities.Board;
import com.itbangmodkradankanbanapi.db1.v3.service.BoardService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class JwtAnonymousAuthFilter extends OncePerRequestFilter {

    @Autowired
    private BoardService boardService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //PUBLIC ACCESS CHECK
        if (HttpMethod.GET.matches(request.getMethod()) && SecurityContextHolder.getContext().getAuthentication() == null && request.getAttribute("Error-Message") == null) {
            List<GrantedAuthority> authorities = new LinkedList<>();
            String boardId = (String) request.getAttribute("boardId");
            Board board = boardService.getBoardById(boardId);
            if (boardService.isPublicAccessibility(board)) {
                authorities.add(new SimpleGrantedAuthority("PUBLIC_ACCESS"));
            } else {
                authorities.add(new SimpleGrantedAuthority("ANONYMOUS"));
                request.setAttribute("Error-Message", "Unauthorized access");
            }
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, authorities));
        }
        chain.doFilter(request, response);
    }


}