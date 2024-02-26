package com.example.payment_service.client.orders;

import com.example.payment_service.client.orders.dto.request.OrdersStatusUpdateRequestDto;
import com.example.payment_service.client.orders.dto.response.OrderDetailsResponseDto;
import com.example.payment_service.client.orders.dto.response.OrderSoftDeleteResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "orderClient", url = "${feign.orderClient.url}")
public interface OrdersClient {

  @RequestMapping(method = RequestMethod.GET, value = "/api/v1/orders/{orderId}", consumes = "application/json")
  OrderDetailsResponseDto getOrderDetails(@PathVariable("orderId") Long orderId);

  @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/orders/{orderId}/status")
  void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrdersStatusUpdateRequestDto requestDto);

  @RequestMapping(method = RequestMethod.DELETE, value = "/api/v1/orders/{orderId}/cancel")
  OrderSoftDeleteResponseDto softDeleteOrder(@PathVariable("orderId") Long orderId);
}

