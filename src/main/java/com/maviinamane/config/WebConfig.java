package com.maviinamane.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    @Value("${app.cors-origin}")
    private String corsOrigins;

    public WebConfig(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        Paths.get(uploadDir).toAbsolutePath().toUri().toString());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(corsOrigins.split(","))
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}