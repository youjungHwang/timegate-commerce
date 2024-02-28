package com.example.product_service.product.repository;

import com.example.product_service.product.entity.Product;
import com.example.product_service.product.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByProductTypeAndDeletedAtIsNull(ProductType productType, Pageable pageable);
}


