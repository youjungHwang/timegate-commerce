package com.example.product_service.product.dto.response;

import com.example.product_service.product.enums.ProductType;

import java.math.BigDecimal;

public record ReservedProductUpdateResponseDto(Long id,
                                               String productName,
                                               BigDecimal price,
                                               ProductType productType
) {}
