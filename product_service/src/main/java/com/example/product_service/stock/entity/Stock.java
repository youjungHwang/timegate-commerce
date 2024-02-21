package com.example.product_service.stock.entity;

import com.example.product_service.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@Entity
public class Stock extends BaseTimeEntity {
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
