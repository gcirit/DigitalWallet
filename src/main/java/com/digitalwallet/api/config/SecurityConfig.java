package com.digitalwallet.api.config;

import com.digitalwallet.api.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                // Customer management - EMPLOYEE and ADMIN can manage customers
                .requestMatchers("/api/customers/**").hasAnyRole("EMPLOYEE", "ADMIN")
                // Employee management - only ADMIN can manage employees
                .requestMatchers("/api/employees/**").hasRole("ADMIN")
                // Customer-specific endpoints
                .requestMatchers("/api/customers/me/**").hasRole("CUSTOMER")
                .requestMatchers("/api/wallets/me/**").hasRole("CUSTOMER")
                .requestMatchers("/api/transactions/me/**").hasRole("CUSTOMER")
                // Wallet endpoints - allow CUSTOMER, EMPLOYEE, and ADMIN roles
                .requestMatchers("/api/wallets/**").hasAnyRole("CUSTOMER", "EMPLOYEE", "ADMIN")
                // Transaction endpoints - allow CUSTOMER, EMPLOYEE, and ADMIN roles
                .requestMatchers("/api/transactions/**").hasAnyRole("CUSTOMER", "EMPLOYEE", "ADMIN")
                // Allow all other requests for now
                .anyRequest().authenticated()
            )
            .authenticationProvider(customAuthenticationProvider)
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Login successful\",\"user\":\"" + authentication.getName() + "\"}");
                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Authentication failed\"}");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Logout successful\"}");
                })
                .permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            );

        return http.build();
    }
} 