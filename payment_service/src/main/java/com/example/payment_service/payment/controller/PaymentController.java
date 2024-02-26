package com.example.payment_service.payment.controller;

import com.example.payment_service.common.dto.response.ApiResponse;
import com.example.payment_service.payment.dto.request.PaymentAttemptRequestDto;
import com.example.payment_service.payment.dto.response.PaymentAttemptResponseDto;
import com.example.payment_service.payment.dto.response.PaymentSoftDeleteResponseDto;
import com.example.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/payment-service/api/v1")
@RestController
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * 결제 시도
     */
    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentAttemptResponseDto>> attemptPayment(
            @RequestBody PaymentAttemptRequestDto paymentAttemptRequestDto) {
        PaymentAttemptResponseDto responseDto = paymentService.attemptPayment(paymentAttemptRequestDto);
        ApiResponse<PaymentAttemptResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "결제 시도 성공",
                responseDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 취소
     */
    @DeleteMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<PaymentSoftDeleteResponseDto>> softDeletePayment(@PathVariable Long paymentId) {
        PaymentSoftDeleteResponseDto responseDto = paymentService.softDeletePayment(paymentId);
        ApiResponse<PaymentSoftDeleteResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "결제 취소 성공",
                responseDto
        );
        return ResponseEntity.ok(response);
    }

}
