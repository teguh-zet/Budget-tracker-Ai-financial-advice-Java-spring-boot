package com.budgettracker.security;

import com.budgettracker.exception.UnauthorizedException;
import com.budgettracker.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(7);
        
        try {
            if (jwtUtil.validateToken(token)) {
                Integer userId = jwtUtil.getUserIdFromToken(token);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("USER"))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new UnauthorizedException("Token ini sudah tidak valid atau kadaluarsa");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Token ini sudah tidak valid atau kadaluarsa");
        }
        
        filterChain.doFilter(request, response);
    }
}

