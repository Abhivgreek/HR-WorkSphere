package com.hr.security;

import com.hr.entity.Employee;
import com.hr.repository.EmployeeRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove "Bearer " word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.warn("JWT Token parsing failed: " + e.getMessage());
            }
        }

        // If username is available and no authentication exists in Security Context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                // Validate token
                if (jwtUtil.isTokenValid(jwtToken)) {
                    Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
                    String role = jwtUtil.getRoleFromToken(jwtToken);
                    
                    // Verify user exists in database
                    Optional<Employee> employeeOpt = employeeRepo.findById(userId);
                    if (employeeOpt.isPresent() && employeeOpt.get().isActive()) {
                        Employee employee = employeeOpt.get();
                        
                        // Create authentication token
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                                new UsernamePasswordAuthenticationToken(
                                    employee, null, Collections.singletonList(authority));
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Set authentication in Security Context
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        
                        // Add user details to request attributes for controllers to use
                        request.setAttribute("currentUserId", userId);
                        request.setAttribute("currentUserRole", role);
                        request.setAttribute("currentUserEmail", jwtUtil.getEmailFromToken(jwtToken));
                        request.setAttribute("currentUserName", jwtUtil.getNameFromToken(jwtToken));
                    }
                }
            } catch (Exception e) {
                logger.warn("JWT Authentication failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip JWT validation for these paths
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.equals("/") ||
               path.startsWith("/public") ||
               path.startsWith("/error");
    }
}
