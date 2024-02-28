package com.example.payment_service.client.orders.dto.request;

import com.example.payment_service.client.orders.enums.OrderType;
public record OrderStatusUpdateRequestDto(OrderType ordersType) {
}
