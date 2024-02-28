package com.example.product_service.client.controller;

import com.example.product_service.product.dto.response.ProductDetailsResponseDto;
import com.example.product_service.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@RestController
public class InternalProductController {
    private final ProductService productService;
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsResponseDto> getProductDetails(
            @PathVariable("productId") final Long productId) {
        ProductDetailsResponseDto productDetailsResponse = productService.getProductDetails(productId);
        return ResponseEntity.ok(productDetailsResponse);
    }
}
