package com.eb.spring.boot.search.mariadb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // all endpoints in the application
                .allowedOrigins("http://localhost:9000")  // allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")  // allow these HTTP methods
                .allowedHeaders("*")  // allow all headers
                .allowCredentials(true);  // allow credentials for authenticated sessions
    }
}
