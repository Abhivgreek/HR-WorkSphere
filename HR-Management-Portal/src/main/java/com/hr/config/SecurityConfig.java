package com.hr.config;

import com.hr.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST APIs
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Set session management to stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                .requestMatchers("/error", "/", "/public/**").permitAll()
                
                // Dashboard endpoints - accessible to both roles
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "USER")
                
                // Employee management - Admin only for full CRUD, Users can view their own data
                .requestMatchers(HttpMethod.GET, "/api/employees/me").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/employees/me").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/employees/**").hasRole("ADMIN")
                
                // Leave management - Both roles can create and view their leaves, Admin can manage all
                .requestMatchers(HttpMethod.GET, "/api/leave/requests/user/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/leave/requests").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/leave/summary/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/leave/balance/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/leave/**").hasRole("ADMIN")
                
                // Payroll management - Users can view their own payroll, Admin can manage all
                .requestMatchers(HttpMethod.GET, "/api/payroll/employee/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/payroll/**").hasRole("ADMIN")
                
                // Posts/Announcements - Both roles can view, Admin can create
                .requestMatchers(HttpMethod.GET, "/api/posts/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/posts/**").hasRole("ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow origins
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001", 
            "http://localhost:5173", // Vite default port
            "http://localhost:8080"
        ));
        
        // Allow methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Expose headers (important for JWT)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
