package com.socialplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC yapılandırması.
 * Ana sayfa, statik kaynaklar ve yönlendirmeler burada tanımlanır.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Kök URL'yi index.html'e yönlendirme
        registry.addViewController("/").setViewName("forward:/index.html");
        
        // Swagger UI için yönlendirme
        registry.addRedirectViewController("/docs", "/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Statik kaynaklar için yapılandırma
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        
        // Swagger UI için yapılandırma
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
} 