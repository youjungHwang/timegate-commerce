package com.example.orders_service.orders.service;

import com.example.orders_service.client.product.ProductClient;
import com.example.orders_service.client.product.dto.response.ProductDetailsResponseDto;
import com.example.orders_service.client.stock.StockClient;
import com.example.orders_service.common.handler.exception.CustomException;
import com.example.orders_service.common.handler.exception.ErrorCode;
import com.example.orders_service.orders.dto.request.OrdersCreateRequestDto;
import com.example.orders_service.orders.dto.response.OrdersCreateResponseDto;
import com.example.orders_service.orders.entity.Orders;
import com.example.orders_service.orders.enums.OrdersType;
import com.example.orders_service.orders.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final ProductClient productClient;
    private final StockClient stockClient;

    /**
     * 일반 상품 주문 생성 (결제 페이지(장바구니) 진입 시 요청)
     */
    @Transactional
    public OrdersCreateResponseDto createOrder(final OrdersCreateRequestDto ordersCreateRequestDto) {
        // 재고 서비스에 재고 있는지 요청(feign)
        // [수정] 함수 이름 바꾸고, 주문하고자하는 상품의 수량의 개수가 있는지 체크 -> 불린
        if(!stockClient.checkProductStockExists(ordersCreateRequestDto.productId())) {
            throw new CustomException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        // 상품 서비스에서 상품 디테일 정보 가져 옴 (feign) -> 없으면 예외처리
        ProductDetailsResponseDto productDetails = Optional.ofNullable(productClient.getProductDetails(ordersCreateRequestDto.productId()))
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 주문 객체 생성
        Orders orders = Orders.builder()
                .userId(ordersCreateRequestDto.userId())
                .productId(ordersCreateRequestDto.productId())
                .price(productDetails.price())
                .quantity(ordersCreateRequestDto.quantity())
                .ordersType(OrdersType.INITIATED)
                .build();

        ordersRepository.save(orders);

        // 재고 서비스에 재고 감소 요청 (feign)
        stockClient.decreaseStock(ordersCreateRequestDto.productId(), ordersCreateRequestDto.quantity());

        // [추가] 20% 확률로 실패 (캔슬) - 상태 바꾸고, 다시 재고 증가 요청
        // 남은 80%는 IN_PROGRESS 로 변경

        // 주문 생성 응답 반환
        return orders.toCreateResponseDto();
    }


}

