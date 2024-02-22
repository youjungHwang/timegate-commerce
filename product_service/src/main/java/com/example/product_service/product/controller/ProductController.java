package com.example.product_service.product.controller;

import com.example.product_service.common.dto.response.ApiResponse;
import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.dto.request.ReservedProductUpdateRequestDto;
import com.example.product_service.product.dto.response.ProductUpdateResponseDto;
import com.example.product_service.product.dto.response.ReservedProductUpdateResponseDto;
import com.example.product_service.product.entity.Product;
import com.example.product_service.product.service.ProductService;
import com.example.product_service.product.service.ReservedProductService;
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
    private final ReservedProductService reservedProductService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(Pageable pageable) {
        final Page<Product> productPage = productService.getAllProducts(pageable);
        ApiResponse<Page<Product>> response = new ApiResponse<>(
                HttpStatus.OK,
                "일반 상품 전체 조회 성공",
                productPage
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable final Long productId) {
        Product product = productService.getProductById(productId);
        ApiResponse<Product> response = new ApiResponse<>(
                HttpStatus.OK,
                "일반 상품 한 건 조회 성공",
                product
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
                "일반 상품 정보 수정 성공",
                updatedProductDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}/stock")
    public ResponseEntity<ApiResponse<Long>> getProductStock(@PathVariable final Long productId) {
        Long currentStockCount = productService.getProductStock(productId);
        ApiResponse<Long> response = new ApiResponse<>(
                HttpStatus.OK,
                "일반 상품 남은 재고 조회 성공",
                currentStockCount
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable final Long productId) {
        productService.deleteProduct(productId);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "일반 상품 삭제 성공",
                null
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 예약 상품
     */
    @GetMapping("/reserved-products")
    public ResponseEntity<ApiResponse<Page<Product>>> getAllReservedProducts(Pageable pageable) {
        final Page<Product> productPage = reservedProductService.getAllReservedProducts(pageable);
        ApiResponse<Page<Product>> response = new ApiResponse<>(
                HttpStatus.OK,
                "예약 상품 전체 조회 성공",
                productPage
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reserved-products/{productId}")
    public ResponseEntity<ApiResponse<Product>> getReservedProductById(@PathVariable final Long productId) {
        Product product = reservedProductService.getReservedProductById(productId);
        ApiResponse<Product> response = new ApiResponse<>(
                HttpStatus.OK,
                "예약 상품 한 건 조회 성공",
                product
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reserved-products/{productId}")
    public ResponseEntity<ApiResponse<ReservedProductUpdateResponseDto>> updateReservedProduct(
            @PathVariable final Long productId,
            @RequestBody ReservedProductUpdateRequestDto reservedProductUpdateRequestDto) {
        ReservedProductUpdateResponseDto updatedProductDto = reservedProductService.updateReservedProduct(productId, reservedProductUpdateRequestDto);
        ApiResponse<ReservedProductUpdateResponseDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "예상 상품 정보 수정 성공",
                updatedProductDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reserved-products/{productId}/stock")
    public ResponseEntity<ApiResponse<Long>> getReservedProductStock(@PathVariable final Long productId) {
        Long currentStockCount = reservedProductService.getReservedProductStock(productId);
        ApiResponse<Long> response = new ApiResponse<>(
                HttpStatus.OK,
                "예약 상품 남은 재고 조회 성공",
                currentStockCount
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reserved-products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteReservedProduct(@PathVariable final Long productId) {
        reservedProductService.deleteReservedProduct(productId);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "예약 상품 삭제 성공",
                null
        );
        return ResponseEntity.ok(response);
    }
}