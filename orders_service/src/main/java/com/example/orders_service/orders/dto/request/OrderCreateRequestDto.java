package com.example.orders_service.orders.dto.request;

import java.math.BigDecimal;

public record OrderCreateRequestDto(Long userId,
                                    Long productId,
                                    BigDecimal price,
                                    Long quantity) {
}
