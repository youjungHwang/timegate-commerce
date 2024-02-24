package com.example.stock_service.stock.repository;

import com.example.stock_service.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockRepository extends JpaRepository<Stock, Long> {
    boolean existsByProductId(Long productId);
}
