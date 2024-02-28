package com.example.product_service.product.entity;

import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.enums.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 엔티티 기능 테스트")
class ProductTest {
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .productName("테스트 상품명")
                .price(BigDecimal.valueOf(25000))
                .productType(ProductType.REGULAR)
                .availableFrom(LocalDateTime.now())
                .availableUntil(LocalDateTime.now().plusHours(1))
                .build();
    }

    @DisplayName("일반 상품 정보 업데이트가 정상적으로 처리되는지 확인")
    @Test
    void updateProduct(){
        // given
        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto(
                "업데이트된 일반 상품명",
                new BigDecimal(22000),
                ProductType.REGULAR
                );

        // when
        product.updateProduct(productUpdateRequestDto);

        // then
        assertThat(product.getProductName()).isEqualTo("업데이트된 일반 상품명");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal(22000));
        assertThat(product.getProductType()).isEqualTo(ProductType.REGULAR);
    }

    @DisplayName("예약 상품 정보 업데이트가 정상적으로 처리되는지 확인")
    @Test
    void updateReservedProduct(){
        // given
        ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto(
                "업데이트된 예약 상품명",
                new BigDecimal(1000),
                ProductType.RESERVED
        );

        // when
        product.updateProduct(productUpdateRequestDto);

        // then
        assertThat(product.getProductName()).isEqualTo("업데이트된 예약 상품명");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal(1000));
        assertThat(product.getProductType()).isEqualTo(ProductType.RESERVED);
    }

    @DisplayName("상품 소프트 딜리트가 정상적으로 처리되는지 확인")
    @Test
    void softDelete(){
        // given
        assertThat(product.getDeletedAt()).isNull();

        // when
        product.softDelete();

        // then
        assertThat(product.getDeletedAt()).isNotNull();
        long secondsDifference = ChronoUnit.SECONDS.between(product.getDeletedAt(), LocalDateTime.now());
        assertThat(secondsDifference).isLessThanOrEqualTo(5);
    }
}