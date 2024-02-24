package com.example.orders_service.orders.dto.request;

import java.math.BigDecimal;

public record OrdersCreateRequestDto(Long userId,
                                     Long productId,
                                     BigDecimal price,
                                     Long quantity) {
}
