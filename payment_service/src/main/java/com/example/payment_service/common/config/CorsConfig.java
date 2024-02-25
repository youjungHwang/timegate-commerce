package com.example.payment_service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/user-service/api/**", config);
        source.registerCorsConfiguration("/orders-service/api/**", config);
        source.registerCorsConfiguration("/product-service/api/**", config);
        source.registerCorsConfiguration("/payment-service/api/**", config);
        source.registerCorsConfiguration("/stock-service/api/**", config);
        return new CorsFilter(source);
    }
}
