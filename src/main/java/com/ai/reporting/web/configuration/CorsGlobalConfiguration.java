package com.ai.reporting.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class CorsGlobalConfiguration implements WebFluxConfigurer {
    private final String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(this.allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowCredentials(true)
                .maxAge(600);
    }

    public CorsGlobalConfiguration(
            @Value("${app.cors.allowed_origins}") String allowedOriginsString) {
        this.allowedOrigins = allowedOriginsString.split(",");
    }
}
