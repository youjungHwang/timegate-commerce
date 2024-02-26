package com.example.orders_service.client.controller;

import com.example.orders_service.client.dto.request.OrdersStatusUpdateRequestDto;
import com.example.orders_service.client.dto.response.OrderDetailsResponseDto;
import com.example.orders_service.orders.dto.response.OrderSoftDeleteResponseDto;
import com.example.orders_service.orders.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@RestController
public class InternalOrdersController {
    private final OrdersService ordersService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponseDto> getOrderDetails(
            @PathVariable("orderId") final Long orderId) {
        OrderDetailsResponseDto responseDto = ordersService.getOrderDetails(orderId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable("orderId") final Long orderId, @RequestBody OrdersStatusUpdateRequestDto requestDto) {
        ordersService.updateOrderStatus(orderId, requestDto);

        return ResponseEntity.ok().body("status change success");
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<OrderSoftDeleteResponseDto> softDeleteOrder(
            @PathVariable("orderId") Long orderId) {
        OrderSoftDeleteResponseDto responseDto = ordersService.softDeleteOrder(orderId);
        return ResponseEntity.ok().body(responseDto);
    }

}
