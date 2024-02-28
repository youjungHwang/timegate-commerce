package com.example.payment_service.client.stock;

import com.example.payment_service.client.stock.dto.StockRequestDto;
import com.example.payment_service.client.stock.dto.StockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "stockClient", url = "${feign.stockClient.url}")
public interface StockClient {

    // 재고 증가 요청
    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/stocks/increase", consumes = "application/json")
    StockResponseDto increaseProductStock(@RequestBody StockRequestDto requestDto);

}
