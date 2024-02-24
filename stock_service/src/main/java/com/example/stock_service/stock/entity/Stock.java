package com.example.stock_service.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@Entity
public class Stock{
    @Id
    @Column(name = "product_id")
    private Long productId;

    private Long stock;

    public Stock(Long productId, Long stock) {
        this.productId = productId;
        this.stock = stock;
    }

    public void updateStocks(Long stock){
        this.stock = stock;
    }
}
