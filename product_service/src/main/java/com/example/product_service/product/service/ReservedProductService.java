package com.example.product_service.product.service;

import com.example.product_service.common.handler.exception.CustomException;
import com.example.product_service.common.handler.exception.ErrorCode;
import com.example.product_service.product.dto.request.ReservedProductUpdateRequestDto;
import com.example.product_service.product.dto.response.ReservedProductUpdateResponseDto;
import com.example.product_service.product.entity.Product;
import com.example.product_service.product.enums.ProductType;
import com.example.product_service.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservedProductService {
    private final ProductRepository productRepository;
    @Transactional(readOnly = true)
    public Page<Product> getAllReservedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllByProductTypeAndDeletedAtIsNull(ProductType.RESERVED, pageable);
        if (products.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public Product getReservedProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ReservedProductUpdateResponseDto updateReservedProduct(
            Long productId,
            ReservedProductUpdateRequestDto reservedProductUpdateRequestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateReservedProduct(reservedProductUpdateRequestDto);

        productRepository.save(product);

        return new ReservedProductUpdateResponseDto(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getProductType()
        );
    }

    @Transactional
    public void deleteReservedProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getProductType().equals(ProductType.RESERVED)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        productRepository.delete(product);
    }

}
