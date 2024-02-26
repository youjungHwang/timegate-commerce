package com.example.payment_service.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSoftDeleteResponseDto(Long paymentId,
                                           Long userId,
                                           Long orderId,
                                           BigDecimal price,
                                           Long quantity,
                                           LocalDateTime deletedAt) {
}
