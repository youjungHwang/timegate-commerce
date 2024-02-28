package com.example.product_service.product.entity;

import com.example.product_service.common.entity.BaseTimeEntity;
import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.dto.request.ReservedProductUpdateRequestDto;
import com.example.product_service.product.enums.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private LocalDateTime availableFrom;

    private LocalDateTime availableUntil;

    private LocalDateTime deletedAt;

    public void updateProduct(ProductUpdateRequestDto requestDto){
        this.productName = requestDto.productName();
        this.price = requestDto.price();
        this.productType = requestDto.productType();
    }

    public void updateReservedProduct(ReservedProductUpdateRequestDto requestDto) {
        this.productName = requestDto.productName();
        this.price = requestDto.price();
        this.productType = requestDto.productType();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

}
