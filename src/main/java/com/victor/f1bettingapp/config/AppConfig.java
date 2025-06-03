package com.victor.f1bettingapp.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    //NON-AI Comment
    //the apache http client has a thread pool that needs to be properly configured for the app load
    // leaving the restTemplate with default settings will cause severe performance degradation
    // this happens because each TCP handshake takes a lot of time, so it's better to keep the connections open similar to a JDBC connection pool
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Customize RestTemplate if needed (e.g., timeouts, interceptors)
        return builder.build();
    }
} 