package com.digitalwallet.api.security;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Attempting authentication for username: {}", username);

        // First try to find as customer (by TCKN)
        Customer customer = customerRepository.findByTckn(username).orElse(null);
        if (customer != null) {
            // For now, accept any password (in production, you'd want proper password validation)
            String role = "ROLE_CUSTOMER";
            
            log.info("Customer authentication successful for TCKN: {} with role: {}", username, role);

            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        }

        // If not found as customer, try as employee (by employeeId)
        Employee employee = employeeRepository.findByEmployeeId(username).orElse(null);
        if (employee != null) {
            // For now, accept any password (in production, you'd want proper password validation)
            String role = "ROLE_" + employee.getRole().name();
            
            log.info("Employee authentication successful for EmployeeID: {} with role: {}", username, role);

            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        }

        // If neither customer nor employee found
        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 