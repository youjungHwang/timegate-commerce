package com.example.stock_service.client.controller;

import com.example.stock_service.client.dto.request.StockCreateRequestDto;
import com.example.stock_service.client.dto.request.StockRequestDto;
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

    /**
     * 재고 증가 요청
     */
    @PutMapping("/increase")
    public ResponseEntity<StockResponseDto> increaseProductStock(@RequestBody StockRequestDto requestDto) {
        StockResponseDto responseDto = stockService.increaseProductStock(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }

    /**
     * 재고 감소 요청
     */
    @PutMapping("/decrease")
    public ResponseEntity<StockResponseDto> decreaseProductStock(@RequestBody StockRequestDto requestDto) {
        StockResponseDto responseDto = stockService.decreaseProductStock(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }
}
