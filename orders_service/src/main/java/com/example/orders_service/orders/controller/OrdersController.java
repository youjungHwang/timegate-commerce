package com.example.orders_service.orders.controller;

import com.example.orders_service.common.dto.response.ApiResponse;
import com.example.orders_service.orders.dto.request.OrdersCreateRequestDto;
import com.example.orders_service.orders.dto.response.OrderDetailsResponseDto;
import com.example.orders_service.orders.dto.response.OrdersCreateResponseDto;
import com.example.orders_service.orders.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/orders-service/api/v1")
@RestController
public class OrdersController {

    private final OrdersService ordersService;

    /**
     * 일반 상품 주문 생성 (결제 페이지 진입 시 요청)
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrdersCreateResponseDto>> createOrder(
            @RequestBody OrdersCreateRequestDto ordersCreateRequestDto) {
        OrdersCreateResponseDto createdOrdersDto = ordersService.createOrder(ordersCreateRequestDto);
        ApiResponse<OrdersCreateResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "일반 상품 주문 생성 성공",
                createdOrdersDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 일반 상품 주문 조회
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailsResponseDto>> getOrderDetails(
            @PathVariable Long orderId ) {
        OrderDetailsResponseDto responseDto = ordersService.getOrderDetails(orderId);
        ApiResponse<OrderDetailsResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "일반 상품 주문 조회 성공",
                responseDto
        );
        return ResponseEntity.ok(response);
    }


}
