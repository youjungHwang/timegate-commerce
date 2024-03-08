package com.example.payment_service.payment.service;

import com.example.payment_service.client.orders.OrdersClient;
import com.example.payment_service.client.orders.dto.response.OrderDetailsResponseDto;
import com.example.payment_service.client.orders.dto.response.OrderSoftDeleteResponseDto;
import com.example.payment_service.client.orders.enums.OrderType;
import com.example.payment_service.common.handler.exception.CustomException;
import com.example.payment_service.payment.dto.request.PaymentAttemptRequestDto;
import com.example.payment_service.payment.dto.response.PaymentAttemptResponseDto;
import com.example.payment_service.payment.dto.response.PaymentSoftDeleteResponseDto;
import com.example.payment_service.payment.entity.Payment;
import com.example.payment_service.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrdersClient ordersClient;

    @Nested
    @DisplayName("결제 시도 테스트")
    class AttemptPaymentTests {

        /**
         * TODO: 랜덤 로직 관련 처리
         *  랜덤 요소를 배제하고 나머지 부분들이 제대로 동작하는지 확인
         */

        @Test
        @DisplayName("결제 시도 성공")
        void attemptPayment_successfully() {
            // given
            Long orderId = 1L;
            PaymentAttemptRequestDto requestDto = new PaymentAttemptRequestDto(orderId);
            OrderDetailsResponseDto orderDetailsResponseDto = new OrderDetailsResponseDto(
                    1L, 1L, 1L, BigDecimal.valueOf(500),
                    5L, OrderType.IN_PROGRESS
            );

            Payment newPayment = Payment.builder()
                    .userId(orderDetailsResponseDto.userId())
                    .orderId(orderDetailsResponseDto.orderId())
                    .price(orderDetailsResponseDto.price())
                    .quantity(orderDetailsResponseDto.quantity())
                    .build();

            // mocking
            when(ordersClient.getOrderDetails(requestDto.orderId()))
                    .thenReturn(orderDetailsResponseDto);
            when(paymentRepository.save(any(Payment.class)))
                    .thenReturn(newPayment);

            // when
            PaymentAttemptResponseDto responseDto = paymentService.attemptPayment(requestDto);

            // then
            assertEquals(newPayment.getUserId(), responseDto.userId());
            assertEquals(newPayment.getOrderId(), responseDto.orderId());
            assertEquals(newPayment.getPrice(), responseDto.price());
            assertEquals(newPayment.getQuantity(), responseDto.quantity());
        }

        @Test
        @DisplayName("결제 시도 시 주문 상태가 IN_PROGRESS가 아닐 경우 커스텀 예외 발생")
        void attemptPayment_throwsException_IfOrderNotInProgress() {
            // given
            Long orderId = 1L;
            PaymentAttemptRequestDto requestDto = new PaymentAttemptRequestDto(orderId);
            OrderDetailsResponseDto orderDetails = new OrderDetailsResponseDto(
                    orderId, 1L, 1L, BigDecimal.valueOf(500),
                    5L, OrderType.COMPLETED
            );

            // mocking
            when(ordersClient.getOrderDetails(requestDto.orderId()))
                    .thenReturn(orderDetails);

            // when, then
            assertThatThrownBy(() -> paymentService.attemptPayment(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("주문이 진행 중인 상태가 아닙니다.");
        }

    }

    @Nested
    @DisplayName("결제 취소 테스트")
    class SoftDeletePaymentTests {

        @Test
        @DisplayName("결제 취소 성공")
        void softDeletePayment_successfully() {
            // given
            Long paymentId = 1L;
            Payment payment = Payment.builder()
                    .id(paymentId)
                    .userId(1L)
                    .orderId(1L)
                    .price(BigDecimal.valueOf(500))
                    .quantity(5L)
                    .build();

            LocalDateTime now = LocalDateTime.now();
            OrderSoftDeleteResponseDto orderSoftDeleteResponseDto = new OrderSoftDeleteResponseDto(
                    payment.getOrderId(),
                    payment.getUserId(),
                    1L,
                    payment.getPrice(),
                    payment.getQuantity(),
                    OrderType.IN_PROGRESS,
                    now
            );

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
            when(ordersClient.softDeleteOrder(payment.getOrderId())).thenReturn(orderSoftDeleteResponseDto);

            ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

            // when
            PaymentSoftDeleteResponseDto result = paymentService.softDeletePayment(paymentId);

            // then
            assertNotNull(result.deletedAt());
            verify(paymentRepository).save(paymentArgumentCaptor.capture());

            Payment capturedPayment = paymentArgumentCaptor.getValue();
            assertEquals(paymentId, capturedPayment.getId());
            assertNotNull(capturedPayment.getDeletedAt());
        }

        @Test
        @DisplayName("존재하지 않는 결제ID로 결제 취소시 커스텀 예외 발생")
        void softDeletePayment_throwsException_ifPaymentNotFound() {
            // given
            Long paymentId = 999L;

            // mocking
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            // when, then
            assertThrows(CustomException.class, () -> paymentService.softDeletePayment(paymentId));
        }

        @Test
        @DisplayName("이미 취소된 결제에 대한 결제 취소시 커스텀 예외 발생")
        void softDeletePayment_throwsException_ifAlreadyDeleted() {
            // given
            Long paymentId = 1L;
            Payment payment = Payment.builder()
                    .id(paymentId)
                    .userId(1L)
                    .orderId(1L)
                    .price(BigDecimal.valueOf(500))
                    .quantity(5L)
                    .deletedAt(LocalDateTime.now())
                    .build();

            // mocking
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.ofNullable(payment));

            // when, then
            assertThatThrownBy(() -> paymentService.softDeletePayment(paymentId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("이미 취소된 주문입니다.");
        }
    }
}