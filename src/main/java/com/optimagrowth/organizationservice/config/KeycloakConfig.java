package com.optimagrowth.organizationservice.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver(){
        // use the Spring Boot properties file instead of keycloak.json
        // Default: Spring Security Adapter looks for keycloak.json
        return new KeycloakSpringBootConfigResolver();
    }
}
