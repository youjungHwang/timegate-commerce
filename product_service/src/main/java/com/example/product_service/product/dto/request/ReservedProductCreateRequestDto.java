package com.example.product_service.product.dto.request;

import com.example.product_service.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservedProductCreateRequestDto(String productName,
                                              BigDecimal price,
                                              ProductType productType,
                                              LocalDateTime availableFrom,
                                              LocalDateTime availableUntil) {
}
