package com.example.product_service.product.dto.response;

import com.example.product_service.product.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
public record ProductCreateResponseDto(Long productId,
                                       String productName,
                                       BigDecimal price,
                                       Long stock,
                                       ProductType productType) {
}
