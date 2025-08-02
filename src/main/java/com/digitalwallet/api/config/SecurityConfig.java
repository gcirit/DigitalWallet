package com.digitalwallet.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow H2 console
                .requestMatchers("/h2-console/**").permitAll()
                // Allow all requests for now (we'll secure this later)
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                // Disable CSRF for H2 console
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                // Disable frame options for H2 console (new syntax for Spring Security 6.1+)
                .frameOptions(frameOptions -> frameOptions.disable())
            );
        
        return http.build();
    }
} 