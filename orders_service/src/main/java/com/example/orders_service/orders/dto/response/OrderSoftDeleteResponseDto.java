package com.example.orders_service.orders.dto.response;

import com.example.orders_service.orders.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSoftDeleteResponseDto(Long orderId,
                                         Long userId,
                                         Long productId,
                                         BigDecimal price,
                                         Long quantity,
                                         OrderType ordersType,
                                         LocalDateTime deletedAt) {
}
