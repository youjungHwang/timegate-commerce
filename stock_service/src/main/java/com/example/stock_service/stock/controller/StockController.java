package com.example.stock_service.stock.controller;

import com.example.stock_service.common.dto.response.ApiResponse;
import com.example.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/stock-service/api/v1")
@RestController
public class StockController {

    private final StockService stockService;

    @GetMapping("/products/{productId}/stock")
    public ResponseEntity<ApiResponse<Long>> getProductStock(@PathVariable final Long productId) {
        Long currentStockCount = stockService.getProductStock(productId);
        ApiResponse<Long> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 남은 재고 조회 성공",
                currentStockCount
        );
        return ResponseEntity.ok(response);
    }
}
