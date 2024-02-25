package com.example.payment_service.payment.dto.response;

import java.math.BigDecimal;

public record PaymentAttemptResponseDto(Long paymentId,
                                        Long userId,
                                        Long orderId,
                                        BigDecimal price,
                                        Long quantity) {
}
