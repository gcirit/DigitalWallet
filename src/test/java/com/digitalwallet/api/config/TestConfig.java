package com.digitalwallet.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    // This will override the DataInitializer in tests
    @Bean
    @Primary
    public DataInitializer testDataInitializer() {
        return new DataInitializer(null, null) {
            @Override
            public void run(String... args) throws Exception {
                // Do nothing - disable data initialization in tests
            }
        };
    }
} 