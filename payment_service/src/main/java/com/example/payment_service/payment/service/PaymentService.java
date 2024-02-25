package com.example.payment_service.payment.service;

import com.example.payment_service.client.orders.OrdersClient;
import com.example.payment_service.client.orders.dto.request.OrdersStatusUpdateRequestDto;
import com.example.payment_service.client.orders.dto.response.OrderDetailsResponseDto;
import com.example.payment_service.client.orders.enums.OrdersType;
import com.example.payment_service.client.stock.StockClient;
import com.example.payment_service.client.stock.dto.StockRequestDto;
import com.example.payment_service.common.handler.exception.CustomException;
import com.example.payment_service.common.handler.exception.ErrorCode;
import com.example.payment_service.payment.dto.request.PaymentAttemptRequestDto;
import com.example.payment_service.payment.dto.response.PaymentAttemptResponseDto;
import com.example.payment_service.payment.entity.Payment;
import com.example.payment_service.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrdersClient orderClient;
    private final StockClient stockClient;

    /**
     * 결제 시도
     */
    @Transactional
    public PaymentAttemptResponseDto attemptPayment(
            final PaymentAttemptRequestDto paymentAttemptRequestDto){

        // 오더의 상태가 IN_PROGRESS인지 확인(OrderClient) IN_PROGRESS아니면 예외처리
        OrderDetailsResponseDto targetOrder = orderClient.getOrderDetails(paymentAttemptRequestDto.orderId());
        if (!(targetOrder.ordersType().equals(OrdersType.IN_PROGRESS))) {
            throw new CustomException(ErrorCode.ORDER_NOT_IN_PROGRESS);
        }
        // 20%확률로 결제 실패 이탈, 상태 변경(OrderClient로 PUT요청, OrderId)FAILED_CUSTOMER → 재고 증가요청(StockClient)
        if (new Random().nextInt(100) < 20) {
            // 주문 상태를 FAILED_CUSTOMER로 변경
            orderClient.updateOrderStatus(targetOrder.orderId(),
                    new OrdersStatusUpdateRequestDto(OrdersType.FAILED_CUSTOMER));
            // 재고 증가 요청 (productId, stock)
            stockClient.increaseProductStock(
                    new StockRequestDto(targetOrder.productId(), targetOrder.quantity()));

            throw new CustomException(ErrorCode.FAILED_CUSTOMER);
        }
            // 80%는 상태가 COMPLETED 변경 → 결제DB에 데이터 생성*
        orderClient.updateOrderStatus(targetOrder.orderId(),
                new OrdersStatusUpdateRequestDto(OrdersType.COMPLETED));

        Payment newPayment = Payment.builder()
                .userId(targetOrder.userId())
                .orderId(targetOrder.orderId())
                .price(targetOrder.price())
                .quantity(targetOrder.quantity())
                .build();

        Payment savedPayment = paymentRepository.save(newPayment);
        return new PaymentAttemptResponseDto(savedPayment.getId(), savedPayment.getUserId(), savedPayment.getOrderId(), savedPayment.getPrice(), savedPayment.getQuantity());

    }
}
