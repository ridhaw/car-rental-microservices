package com.example.rentalservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI rentalServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rental Service API")
                        .description("Microservice de gestion des locations de voitures")
                        .version("1.0.0"));
    }
}
