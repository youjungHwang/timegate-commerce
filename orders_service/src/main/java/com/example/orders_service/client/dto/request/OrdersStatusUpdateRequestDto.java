package com.example.orders_service.client.dto.request;


import com.example.orders_service.orders.enums.OrdersType;

public record OrdersStatusUpdateRequestDto(OrdersType ordersType) {
}
