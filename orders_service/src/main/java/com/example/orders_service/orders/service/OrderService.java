package com.example.orders_service.orders.service;

import com.example.orders_service.client.dto.request.OrderStatusUpdateRequestDto;
import com.example.orders_service.client.dto.response.OrderDetailsResponseDto;
import com.example.orders_service.client.product.ProductClient;
import com.example.orders_service.client.product.dto.response.ProductDetailsResponseDto;
import com.example.orders_service.client.stock.StockClient;
import com.example.orders_service.client.stock.dto.request.StockRequestDto;
import com.example.orders_service.client.stock.dto.response.StockResponseDto;
import com.example.orders_service.common.handler.exception.CustomException;
import com.example.orders_service.common.handler.exception.ErrorCode;
import com.example.orders_service.orders.dto.request.OrderCreateRequestDto;
import com.example.orders_service.orders.dto.response.OrderSoftDeleteResponseDto;
import com.example.orders_service.orders.dto.response.OrderCreateResponseDto;
import com.example.orders_service.orders.entity.Orders;
import com.example.orders_service.orders.enums.OrderType;
import com.example.orders_service.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository ordersRepository;
    private final ProductClient productClient;
    private final StockClient stockClient;

    /**
     * 일반 상품 주문 생성 (결제 페이지(장바구니) 진입 시 요청)
     */
    @Transactional
    public OrderCreateResponseDto createOrder(final OrderCreateRequestDto ordersCreateRequestDto) {
        // [재고 서비스] 상품 조회에 쓰인 getStock 재사용
        StockResponseDto stockResponse = stockClient.getProductStocks(ordersCreateRequestDto.productId());
        // 주문 요청 수량과 비교 -> 예외처리
        if (stockResponse.stock() < ordersCreateRequestDto.quantity()) {
            throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        // 상품 서비스에서 상품 디테일 정보 가져 옴 (feign) -> 없으면 예외처리
        ProductDetailsResponseDto productDetails = Optional.ofNullable(productClient.getProductDetails(ordersCreateRequestDto.productId()))
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 요청된 가격과 상품의 실제 가격 비교 -> 가격이 다르면 예외처리
        if (ordersCreateRequestDto.price().compareTo(productDetails.price()) != 0) {
            throw new CustomException(ErrorCode.PRICE_MISMATCH);
        }

        // 주문 객체 생성
        Orders order = Orders.builder()
                .userId(ordersCreateRequestDto.userId())
                .productId(ordersCreateRequestDto.productId())
                .price(productDetails.price())
                .quantity(ordersCreateRequestDto.quantity())
                .ordersType(OrderType.INITIATED)
                .build();

        // 주문 객체 저장
        ordersRepository.save(order);

        // [재고 서비스] 재고 감소 요청 (feign)
        stockClient.decreaseProductStock(
                new StockRequestDto(ordersCreateRequestDto.productId(), ordersCreateRequestDto.quantity()));

        // 20% 확률로 실패 CANCEL 상태 변경 -> [재고 서비스] 재고 증가 요청 (feign)
        if (new Random().nextInt(100) < 20) {
            order.updateOrderStatus(OrderType.CANCEL);
            stockClient.increaseProductStock(
                    new StockRequestDto(ordersCreateRequestDto.productId(), ordersCreateRequestDto.quantity()));
        } else {
            // 남은 80%는 IN_PROGRESS 로 변경
            order.updateOrderStatus(OrderType.IN_PROGRESS);
        }

        // 주문 생성 응답 반환
        return new OrderCreateResponseDto(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getPrice(),
                order.getQuantity(),
                order.getOrdersType()
        );
    }

    /**
     * 일반 상품 주문 조회
     */
    @Transactional(readOnly = true)
    public OrderDetailsResponseDto getOrderDetails(final Long orderId) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return new OrderDetailsResponseDto(
                orders.getId(),
                orders.getUserId(),
                orders.getProductId(),
                orders.getPrice(),
                orders.getQuantity(),
                orders.getOrdersType()
        );
    }

    /**
     * 일반 상품 주문 취소
     */
    @Transactional
    public OrderSoftDeleteResponseDto softDeleteOrder(final Long orderId) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (orders.getOrdersType() == OrderType.CANCEL) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELLED);
        }
        if (orders.getOrdersType() == OrderType.FAILED_CUSTOMER) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(orders.getOrdersType());
            throw new CustomException(ErrorCode.ORDER_PAYMENT_FAILED);
        }
        stockClient.increaseProductStock(
                new StockRequestDto(orders.getProductId(), orders.getQuantity()));
        orders.updateOrderStatus(OrderType.CANCEL);

        ordersRepository.save(orders);

        return new OrderSoftDeleteResponseDto(
                orders.getId(),
                orders.getUserId(),
                orders.getProductId(),
                orders.getPrice(),
                orders.getQuantity(),
                orders.getOrdersType(),
                orders.getDeletedAt()
        );
    }

    /**
     * 주문 상태
     */
    @Transactional(readOnly = true)
    public OrderDetailsResponseDto getOrderStatus(final Long orderId){
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        return new OrderDetailsResponseDto(
                orders.getId(),
                orders.getUserId(),
                orders.getProductId(),
                orders.getPrice(),
                orders.getQuantity(),
                orders.getOrdersType()
        );
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto ordersStatusUpdateRequestDto) {
        OrderType newStatus = ordersStatusUpdateRequestDto.ordersType();

        Orders targetOrder = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        targetOrder.updateOrderStatus(newStatus);
    }


}

