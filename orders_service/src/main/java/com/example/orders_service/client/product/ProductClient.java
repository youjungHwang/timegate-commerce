package com.example.orders_service.client.product;

import com.example.orders_service.client.product.dto.response.ProductDetailsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "productClient", url = "${feign.productClient.url}")
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/products/{productId}", consumes = "application/json")
    ProductDetailsResponseDto getProductDetails(@PathVariable("productId") Long productId);

}
