package com.example.product_service.client.stock;

import com.example.product_service.client.stock.dto.request.StockCreateRequestDto;
import com.example.product_service.client.stock.dto.response.StockCreateResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@FeignClient(name = "stockClient", url = "${feign.stockClient.url}")
public interface StockClient {
    // 상품ID로 재고가 있는지 확인 (boolean 반환)
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/stocks/{productId}/exists", consumes = "application/json")
    boolean checkProductStockExists(@PathVariable("productId") Long productId);

    // [createProduct 사용] 재고 생성 요청 보냄
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/stocks", consumes = "application/json")
    StockCreateResponseDto createProductStock(@RequestBody StockCreateRequestDto requestDto);
}


