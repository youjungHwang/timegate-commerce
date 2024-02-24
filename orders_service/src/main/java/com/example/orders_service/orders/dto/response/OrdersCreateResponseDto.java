package com.example.orders_service.orders.dto.response;

import com.example.orders_service.orders.enums.OrdersType;

import java.math.BigDecimal;

public record OrdersCreateResponseDto(Long orderId,
                                      Long userId,
                                      Long productId,
                                      BigDecimal price,
                                      Long quantity,
                                      OrdersType ordersType) {
}
