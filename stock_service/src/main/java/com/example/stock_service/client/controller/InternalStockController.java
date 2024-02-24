package com.example.stock_service.client.controller;

import com.example.stock_service.client.dto.request.StockCreateRequestDto;
import com.example.stock_service.client.dto.response.StockCreateResponseDto;
import com.example.stock_service.client.dto.response.StockResponseDto;
import com.example.stock_service.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
@RestController
public class InternalStockController {
    private final StockService stockService;

    @PutMapping("/decrease/{productId}")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable("productId") final Long productId,
            @RequestBody Long quantity) {
        stockService.decreaseStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    // 수정 해야함
    @GetMapping("/{productId}/exists")
    public ResponseEntity<Boolean> checkProductStockExists(
            @PathVariable("productId") final Long productId) {
        boolean exists = stockService.checkProductStockExists(productId);
        return ResponseEntity.ok(exists);
    }

    /**
     * 재고 생성 요청
     */
    @PostMapping
    public ResponseEntity<StockCreateResponseDto> createProductStock(
            @RequestBody final StockCreateRequestDto requestDto) {
        StockCreateResponseDto response = stockService.createProductStock(requestDto.productId(), requestDto.stock());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 상품의 재고 조회 요청
     */
    @GetMapping("/{productId}")
    public ResponseEntity<StockResponseDto> getProductStocks(
            @PathVariable("productId") final Long productId) {
        StockResponseDto responseDto = stockService.getProductStocks(productId);
        return ResponseEntity.ok().body(responseDto);
    }

    /**
     * 상품 삭제 요청
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProductStocks(
            @PathVariable("productId") final Long productId) {
        stockService.deleteProductStocks(productId);
        return ResponseEntity.ok().build();
    }
}
