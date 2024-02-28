package com.example.payment_service.payment.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("결제 엔티티 기능 테스트")
class PaymentTest {
    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .userId(1L)
                .orderId(3L)
                .price(BigDecimal.valueOf(25000))
                .quantity(12L)
                .build();
    }

    @DisplayName("삭제 시간 설정 시 deletedAt 필드가 현재 시간으로 업데이트 되어야 함")
    @Test
    void testWithDeletedAt() {
        LocalDateTime now = LocalDateTime.now();
        payment = payment.withDeletedAt(now);
        assertEquals(now, payment.getDeletedAt());
    }
}