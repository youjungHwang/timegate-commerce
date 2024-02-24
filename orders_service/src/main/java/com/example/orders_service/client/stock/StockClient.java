package com.example.orders_service.client.stock;

import com.example.orders_service.client.stock.dto.request.StockRequestDto;
import com.example.orders_service.client.stock.dto.response.StockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@FeignClient(name = "stockClient", url = "${feign.stockClient.url}")
public interface StockClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/stocks/decrease/{productId}", consumes = "application/json")
    void decreaseStock(@PathVariable("productId") Long productId, @RequestBody Long quantity);

    // 상품의 재고 조회 요청
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/stocks/{productId}", consumes = "application/json")
    StockResponseDto getProductStocks(@PathVariable("productId") Long productId);

    // 재고 증가 요청
    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/stocks/increase", consumes = "application/json")
    StockResponseDto increaseProductStock(@RequestBody StockRequestDto requestDto);

    // 재고 감소 요청
    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/stocks/decrease", consumes = "application/json")
    StockResponseDto decreaseProductStock(@RequestBody StockRequestDto requestDto);
}


