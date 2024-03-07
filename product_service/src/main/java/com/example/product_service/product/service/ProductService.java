package com.example.product_service.product.service;

import com.example.product_service.client.stock.StockClient;
import com.example.product_service.client.stock.dto.request.StockCreateRequestDto;
import com.example.product_service.client.stock.dto.response.StockCreateResponseDto;
import com.example.product_service.client.stock.dto.response.StockResponseDto;
import com.example.product_service.common.handler.exception.CustomException;
import com.example.product_service.common.handler.exception.ErrorCode;
import com.example.product_service.product.dto.request.ProductCreateRequestDto;
import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.dto.response.ProductCreateResponseDto;
import com.example.product_service.product.dto.response.ProductDetailsResponseDto;
import com.example.product_service.product.dto.response.ProductUpdateResponseDto;
import com.example.product_service.product.entity.Product;
import com.example.product_service.product.enums.ProductType;
import com.example.product_service.product.repository.ProductRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final StockClient stockClient;

    @Transactional
    public ProductCreateResponseDto createProduct(final ProductCreateRequestDto requestDto) {
        // StockClient 재고 생성 요청 (feign)
        StockCreateRequestDto stockCreateRequestDto = new StockCreateRequestDto(
                requestDto.productId(), requestDto.stock());
        StockCreateResponseDto stockCreateResponseDto = stockClient.createProductStock(stockCreateRequestDto);

        Product product = Product.builder()
                .productName(requestDto.productName())
                .price(requestDto.price())
                .productType(requestDto.productType())
                .availableFrom(requestDto.availableFrom())
                .availableUntil(requestDto.availableUntil())
                .build();

        Product savedProduct = productRepository.save(product);

        return new ProductCreateResponseDto(
                savedProduct.getId(),
                savedProduct.getProductName(),
                savedProduct.getPrice(),
                stockCreateResponseDto.stock(),
                savedProduct.getProductType(),
                savedProduct.getAvailableFrom(),
                savedProduct.getAvailableUntil()
        );
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        Page<Product> products =
                productRepository.findAllByProductTypeAndDeletedAtIsNull(ProductType.REGULAR, pageable);
        if (products.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponseDto getProductDetails(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getDeletedAt() != null) {
                throw new CustomException(ErrorCode.DELETED_ITEM);
            }

            // StockClient 재고 조회 (feign)
            StockResponseDto stockResponse = stockClient.getProductStocks(productId);

            return new ProductDetailsResponseDto(
                    product.getId(),
                    product.getProductName(),
                    product.getPrice(),
                    stockResponse.stock(),
                    product.getProductType(),
                    product.getAvailableFrom(),
                    product.getAvailableUntil()
            );
        } catch (FeignException e) {
            throw new CustomException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

    @Transactional
    public ProductUpdateResponseDto updateProduct(
            Long productId,
            ProductUpdateRequestDto productUpdateRequestDto) {

        if (productUpdateRequestDto.productName() == null || productUpdateRequestDto.productName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        if (productUpdateRequestDto.price() != null && productUpdateRequestDto.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateProduct(productUpdateRequestDto);
        productRepository.save(product);

        return new ProductUpdateResponseDto(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getProductType()
        );
    }


    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        stockClient.deleteProductStocks(productId);
        product.softDelete();
        productRepository.save(product);
    }

}
