package com.example.orders_service.client.stock;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@FeignClient(name = "stockClient", url = "${feign.stockClient.url}")
public interface StockClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/stocks/decrease/{productId}", consumes = "application/json")
    void decreaseStock(@PathVariable("productId") Long productId, @RequestBody Long quantity);

    // 상품ID로 재고가 있는지 확인 (boolean 반환)
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/stocks/{productId}/exists", consumes = "application/json")
    boolean checkProductStockExists(@PathVariable("productId") Long productId);

}


