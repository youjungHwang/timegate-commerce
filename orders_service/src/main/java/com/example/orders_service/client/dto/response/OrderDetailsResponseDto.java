package com.example.orders_service.client.dto.response;

import com.example.orders_service.orders.enums.OrderType;
import java.math.BigDecimal;
public record OrderDetailsResponseDto(Long orderId,
                                      Long userId,
                                      Long productId,
                                      BigDecimal price,
                                      Long quantity,
                                      OrderType ordersType) {
}
