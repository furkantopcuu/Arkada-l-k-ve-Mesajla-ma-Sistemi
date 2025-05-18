package com.socialplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sosyal Platform API")
                        .description("Arkadaşlık ve Mesajlaşma Sistemi API Dokümantasyonu")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Sosyal Platform Ekibi")
                                .url("https://socialplatform.com")
                                .email("info@socialplatform.com")));
    }
} 