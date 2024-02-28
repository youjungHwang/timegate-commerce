package com.example.orders_service.client.product.dto.response;

import com.example.orders_service.client.product.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDetailsResponseDto(Long productId,
                                        String productName,
                                        BigDecimal price,
                                        ProductType productType,
                                        LocalDateTime availableFrom,
                                        LocalDateTime availableUntil) {
}


