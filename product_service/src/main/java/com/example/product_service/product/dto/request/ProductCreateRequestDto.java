package com.example.product_service.product.dto.request;

import com.example.product_service.product.enums.ProductType;
import java.math.BigDecimal;

public record ProductCreateRequestDto(Long productId,
                                      String productName,
                                      BigDecimal price,
                                      Long stock,
                                      ProductType productType) {
}
