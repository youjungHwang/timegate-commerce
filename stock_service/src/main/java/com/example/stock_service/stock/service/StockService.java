package com.example.stock_service.stock.service;

import com.example.stock_service.client.dto.request.StockRequestDto;
import com.example.stock_service.client.dto.response.StockCreateResponseDto;
import com.example.stock_service.client.dto.response.StockResponseDto;
import com.example.stock_service.common.handler.exception.CustomException;
import com.example.stock_service.common.handler.exception.ErrorCode;
import com.example.stock_service.stock.entity.Stock;
import com.example.stock_service.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StockService {

    private final StockRepository stockRepository;
    @Transactional(readOnly = true)
    public Long getProductStock(final Long productId) {
        Stock stocks = stockRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));

        return stocks.getStock();
    }

    /**
     * 1. 요청이 한 개씩 들어오는 상황 -> 요청이 동시에 여러개가 온다면?
     */
    @Transactional
    public void decreaseStock(final Long productId, final Long quantity) {
        Stock stock = stockRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 현재 재고 수량을 확인하고, 요청된 수량만큼 감소
        Long currentStock = stock.getStock();
        if (currentStock < quantity) {
            throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        // 재고 수량을 감소시키고 업데이트
        stock.updateStocks(currentStock - quantity);
        stockRepository.save(stock);
    }

    /**
     * 재고 생성
     */
    @Transactional
    public StockCreateResponseDto createProductStock(final Long productId, final Long quantity) {
        // 상품 ID로 이미 재고가 있는지 확인
        boolean exists = stockRepository.existsByProductId(productId);
        if (exists) {
            throw new CustomException(ErrorCode.STOCK_ALREADY_EXISTS);
        }
        // 재고 객체를 생성
        Stock stock = new Stock(productId, quantity);

        Stock savedStock = stockRepository.save(stock);

        return new StockCreateResponseDto(savedStock.getProductId(), savedStock.getStock());
    }

    /**
     * 상품의 재고 조회 요청
     */
    @Transactional(readOnly = true)
    public StockResponseDto getProductStocks(final Long productId) {
        Stock stock = stockRepository.findById(productId)
                .filter(s -> s.getStock() > 0)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));
        return new StockResponseDto(stock.getProductId(), stock.getStock());
    }

    /**
     * 상품 삭제 요청
     */
    @Transactional
    public void deleteProductStocks(final Long productId) {
        Stock stock = stockRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));
        stockRepository.delete(stock);
    }

    /**
     * 재고 증가 요청
     */
    @Transactional
    public StockResponseDto increaseProductStock(StockRequestDto requestDto) {
        if (requestDto.stock() <= 0) {
            throw new CustomException(ErrorCode.INVALID_STOCK_QUANTITY);
        }

        Stock stock = stockRepository.findById(requestDto.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        Long newStockQuantity = stock.getStock() + requestDto.stock();

        Stock updatedStock = stockRepository.save(Stock.builder()
                .productId(stock.getProductId())
                .stock(newStockQuantity)
                .build());

        return new StockResponseDto(updatedStock.getProductId(), updatedStock.getStock());
    }

    /**
     * 재고 감소 요청
     */
    @Transactional
    public StockResponseDto decreaseProductStock(StockRequestDto requestDto) {
        Stock stock = stockRepository.findById(requestDto.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        Long newStockQuantity = stock.getStock() - requestDto.stock();

        if(newStockQuantity < 0) {
            throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        Stock updatedStock = stockRepository.save(Stock.builder()
                .productId(stock.getProductId())
                .stock(newStockQuantity)
                .build());

        return new StockResponseDto(updatedStock.getProductId(), updatedStock.getStock());
    }
}
