package com.example.product_service.product.dto.response;

import com.example.product_service.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDetailsResponseDto(Long productId,
                                        String productName,
                                        BigDecimal price,
                                        Long stock,
                                        ProductType productType,
                                        LocalDateTime availableFrom,
                                        LocalDateTime availableUntil) {
}