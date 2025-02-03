package com.maveric.loanapi.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.maveric.loanapi.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

    	 if (request.getRequestURI().equals("/api/v1/auth/login") || request.getRequestURI().equals("/api/v1/otp/send")) {
             chain.doFilter(request, response);  // Continue without filtering
             return;
         }

         String token = request.getHeader("Authorization");

         if (token != null && token.startsWith("Bearer ")) {
             token = token.substring(7);  // Remove "Bearer " prefix
             String username = jwtUtil.extractUsername(token);

             if (username != null && jwtUtil.validateToken(token, username)) {
                 SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, null));
             }
         }

         chain.doFilter(request, response); 
     }
}
