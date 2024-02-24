package com.example.orders_service.orders.controller;

import com.example.orders_service.common.dto.response.ApiResponse;
import com.example.orders_service.orders.dto.request.OrdersCreateRequestDto;
import com.example.orders_service.orders.dto.response.OrdersCreateResponseDto;
import com.example.orders_service.orders.entity.Orders;
import com.example.orders_service.orders.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/orders-service/api/v1")
@RestController
public class OrdersController {

    private final OrdersService ordersService;
    /**
     * 일반 상품 주문 생성하기 /orders -> createOrder
     * 일반 상품 주문 조회하기 /orders/{orderId} -> getOrderById
     * 일반 상품 주문 취소하기 /orders/{orderId} → cancelOrder -> 재고 복구
     *
     * 예약 상품 주문 생성하기 /orders -> createReservedProductOrder  -> 예약 시간 내인지 확인
     * 예약 상품 주문 조회하기 /orders/{orderId} -> getReservedProductOrderById
     * 예약 상품 주문 취소하기 /orders/{orderId} cancelReservedProductOrder → 재고 복구
     */

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


}
