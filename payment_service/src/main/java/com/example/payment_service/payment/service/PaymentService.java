package com.example.payment_service.payment.service;

import com.example.payment_service.client.orders.OrdersClient;
import com.example.payment_service.client.orders.dto.request.OrderStatusUpdateRequestDto;
import com.example.payment_service.client.orders.dto.response.OrderDetailsResponseDto;
import com.example.payment_service.client.orders.dto.response.OrderSoftDeleteResponseDto;
import com.example.payment_service.client.orders.enums.OrderType;
import com.example.payment_service.client.stock.StockClient;
import com.example.payment_service.client.stock.dto.StockRequestDto;
import com.example.payment_service.common.handler.exception.CustomException;
import com.example.payment_service.common.handler.exception.ErrorCode;
import com.example.payment_service.payment.dto.request.PaymentAttemptRequestDto;
import com.example.payment_service.payment.dto.response.PaymentAttemptResponseDto;
import com.example.payment_service.payment.dto.response.PaymentSoftDeleteResponseDto;
import com.example.payment_service.payment.entity.Payment;
import com.example.payment_service.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        // 주문 상태가 IN_PROGRESS인지 확인 (OrderClient)
        OrderDetailsResponseDto targetOrder = orderClient.getOrderDetails(paymentAttemptRequestDto.orderId());
        if (!(targetOrder.ordersType().equals(OrderType.IN_PROGRESS))) {
            throw new CustomException(ErrorCode.ORDER_NOT_IN_PROGRESS);
        }

        // 20%확률로 결제 실패 이탈, 상태 변경 FAILED_CUSTOMER → 재고 증가요청(StockClient)
        if (new Random().nextInt(100) < 20) {
            // 주문 상태를 FAILED_CUSTOMER로 변경
            orderClient.updateOrderStatus(targetOrder.orderId(),
                    new OrderStatusUpdateRequestDto(OrderType.FAILED_CUSTOMER));
            // 재고 증가 요청
            stockClient.increaseProductStock(
                    new StockRequestDto(targetOrder.productId(), targetOrder.quantity()));

            throw new CustomException(ErrorCode.FAILED_CUSTOMER);
        }

        // 80% 상태 COMPLETED 변경 → 결제DB에 데이터 생성
        orderClient.updateOrderStatus(targetOrder.orderId(),
                new OrderStatusUpdateRequestDto(OrderType.COMPLETED));

        Payment newPayment = Payment.builder()
                .userId(targetOrder.userId())
                .orderId(targetOrder.orderId())
                .price(targetOrder.price())
                .quantity(targetOrder.quantity())
                .build();

        Payment savedPayment = paymentRepository.save(newPayment);
        return new PaymentAttemptResponseDto(savedPayment.getId(), savedPayment.getUserId(), savedPayment.getOrderId(), savedPayment.getPrice(), savedPayment.getQuantity());

    }

    /**
     * 결제 취소
     */
    @Transactional
    public PaymentSoftDeleteResponseDto softDeletePayment(final Long paymentId){
        // 결제ID를 통해 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        // 결제ID안에 deleted_at이 null이 아니면 이미 취소된 결제입니다.
        if(payment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_PAYMENT);
        }

        // OderClient 주문 취소 요청
        OrderSoftDeleteResponseDto orderResponse = orderClient.softDeleteOrder(payment.getOrderId());

        // deleted_at(소프트 딜리트) → 결제DB 업데이트
        payment = payment.withDeletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return new PaymentSoftDeleteResponseDto(
                payment.getId(),
                payment.getUserId(),
                payment.getOrderId(),
                payment.getPrice(),
                payment.getQuantity(),
                payment.getDeletedAt()
        );

    }

}
