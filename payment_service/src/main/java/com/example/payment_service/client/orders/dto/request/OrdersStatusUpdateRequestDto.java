package com.example.payment_service.client.orders.dto.request;

import com.example.payment_service.client.orders.enums.OrdersType;

public record OrdersStatusUpdateRequestDto(OrdersType ordersType) {
}
