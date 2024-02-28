package com.example.payment_service.client.orders.dto.response;

import com.example.payment_service.client.orders.enums.OrderType;
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
