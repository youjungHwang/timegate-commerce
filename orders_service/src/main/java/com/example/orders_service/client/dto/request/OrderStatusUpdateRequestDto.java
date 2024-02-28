package com.example.orders_service.client.dto.request;

import com.example.orders_service.orders.enums.OrderType;
public record OrderStatusUpdateRequestDto(OrderType ordersType) {
}
