package com.example.orders_service.orders.controller;

import com.example.orders_service.client.dto.response.OrderDetailsResponseDto;
import com.example.orders_service.common.dto.response.ApiResponse;
import com.example.orders_service.orders.dto.request.OrderCreateRequestDto;

import com.example.orders_service.orders.dto.response.OrderSoftDeleteResponseDto;
import com.example.orders_service.orders.dto.response.OrderCreateResponseDto;
import com.example.orders_service.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders-service/api/v1")
@RestController
public class OrderController {

    private final OrderService ordersService;

    /**
     * 주문 생성 (결제 페이지 진입 시 요청)
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(
            @RequestBody OrderCreateRequestDto ordersCreateRequestDto) {
        OrderCreateResponseDto createdOrdersDto = ordersService.createOrder(ordersCreateRequestDto);
        ApiResponse<OrderCreateResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "주문 생성 성공",
                createdOrdersDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주문 조회
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailsResponseDto>> getOrderDetails(
            @PathVariable Long orderId ) {
        OrderDetailsResponseDto responseDto = ordersService.getOrderDetails(orderId);
        ApiResponse<OrderDetailsResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 조회 성공",
                responseDto
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 취소
     */
    @DeleteMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderSoftDeleteResponseDto> softDeleteOrder(
            @PathVariable("orderId") Long orderId) {
        OrderSoftDeleteResponseDto responseDto = ordersService.softDeleteOrder(orderId);
        return ResponseEntity.ok().body(responseDto);
    }

}
