package com.example.payment_service.client.orders.dto.response;

import com.example.payment_service.client.orders.enums.OrdersType;

import java.math.BigDecimal;

public record OrderDetailsResponseDto(Long orderId,
                                      Long userId,
                                      Long productId,
                                      BigDecimal price,
                                      Long quantity,
                                      OrdersType ordersType) {
}
