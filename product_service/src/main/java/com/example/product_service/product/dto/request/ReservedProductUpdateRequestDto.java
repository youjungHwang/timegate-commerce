package com.example.product_service.product.dto.request;

import com.example.product_service.product.enums.ProductType;

import java.math.BigDecimal;
public record ReservedProductUpdateRequestDto(String productName,
                                              BigDecimal price,
                                              ProductType productType
) {}
