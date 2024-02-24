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

// 일반 상품, 예약 상품 공통 사용
// 만약 일반 상품 타입이면, availavle 시간은 null처리 -> 안나오게
