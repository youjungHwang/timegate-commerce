package com.example.product_service.product.controller;

import com.example.product_service.common.dto.response.ApiResponse;
import com.example.product_service.product.dto.request.ProductCreateRequestDto;
import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.dto.response.*;
import com.example.product_service.product.entity.Product;
import com.example.product_service.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/product-service/api/v1")
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(
            @RequestBody final ProductCreateRequestDto productCreateRequestDto) {
        ProductCreateResponseDto productDto = productService.createProduct(productCreateRequestDto);
        ApiResponse<ProductCreateResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 생성 성공",
                productDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(Pageable pageable) {
        final Page<Product> productPage = productService.getAllProducts(pageable);
        ApiResponse<Page<Product>> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 전체 조회 성공",
                productPage
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailsResponseDto>> getProductDetails(@PathVariable Long productId){
        ProductDetailsResponseDto responseDto = productService.getProductDetails(productId);
        ApiResponse<ProductDetailsResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 상세 조회 성공",
                responseDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailsResponseDto>> getProductById(@PathVariable final Long productId) {
        ProductDetailsResponseDto productDetails = productService.getProductDetails(productId);
        ApiResponse<ProductDetailsResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 한 건 조회 성공",
                productDetails
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductUpdateResponseDto>> updateProduct(
            @PathVariable final Long productId,
            @RequestBody ProductUpdateRequestDto productUpdateRequestDto) {
        ProductUpdateResponseDto updatedProductDto = productService.updateProduct(productId, productUpdateRequestDto);
        ApiResponse<ProductUpdateResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 정보 수정 성공",
                updatedProductDto
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable final Long productId) {
        productService.deleteProduct(productId);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 삭제 성공",
                null
        );

        return ResponseEntity.ok(response);
    }
}
