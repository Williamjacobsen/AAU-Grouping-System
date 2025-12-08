package com.aau.grouping_system.Config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SpaRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();

        // LOGIC: 
        // 1. "startsWith" checks ensure we don't interfere with your Backend API or WebSockets.
        // 2. "contains(.)" is a simple check for file extensions (like .js, .css, .html).
        //    If a path has a dot, we assume it's a static file and let Spring serve it directly.
        if (!path.startsWith("/api") && 
            !path.startsWith("/ws") && 
            !path.contains(".")) {
            
            // Forward this request to the frontend (index.html)
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        // Otherwise, continue the chain (serve the API or the static file)
        filterChain.doFilter(request, response);
    }
}