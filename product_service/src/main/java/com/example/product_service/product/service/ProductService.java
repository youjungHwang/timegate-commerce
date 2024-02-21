package com.example.product_service.product.service;

import com.example.product_service.common.handler.exception.CustomException;
import com.example.product_service.common.handler.exception.ErrorCode;
import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.dto.response.ProductUpdateResponseDto;
import com.example.product_service.product.entity.Product;
import com.example.product_service.product.enums.ProductType;
import com.example.product_service.product.repository.ProductRepository;
import com.example.product_service.product.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.product_service.stock.entity.Stock;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllByProductType(ProductType.REGULAR, pageable);
        if (products.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }
    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ProductUpdateResponseDto updateProduct(
            Long productId,
            ProductUpdateRequestDto productUpdateRequestDto) {
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

    @Transactional(readOnly = true)
    public Long getProductStock(Long productId) {
        Stock stocks = stockRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));

        return stocks.getStock();
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getProductType().equals(ProductType.REGULAR)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        productRepository.delete(product);
    }
}
