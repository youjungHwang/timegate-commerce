package com.example.payment_service.payment.dto.request;

import java.math.BigDecimal;

public record PaymentAttemptRequestDto(
                                       Long orderId
                                       ) {
}
