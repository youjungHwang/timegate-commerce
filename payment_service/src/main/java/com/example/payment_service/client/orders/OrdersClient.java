package com.example.payment_service.client.orders;

import com.example.payment_service.client.orders.dto.request.OrdersStatusUpdateRequestDto;
import com.example.payment_service.client.orders.dto.response.OrderDetailsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "orderClient", url = "${feign.orderClient.url}")
public interface OrdersClient {

  @RequestMapping(method = RequestMethod.GET, value = "/api/v1/orders/{orderId}", consumes = "application/json")
  OrderDetailsResponseDto getOrderDetails(@PathVariable("orderId") Long orderId);

  @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/orders/{orderId}/status")
  void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrdersStatusUpdateRequestDto requestDto);
}

