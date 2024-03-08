package com.example.orders_service.orders.service;

import com.example.orders_service.client.dto.request.OrderStatusUpdateRequestDto;
import com.example.orders_service.client.dto.response.OrderDetailsResponseDto;
import com.example.orders_service.client.product.ProductClient;
import com.example.orders_service.client.product.dto.response.ProductDetailsResponseDto;
import com.example.orders_service.client.product.enums.ProductType;
import com.example.orders_service.client.stock.StockClient;
import com.example.orders_service.client.stock.dto.request.StockRequestDto;
import com.example.orders_service.client.stock.dto.response.StockResponseDto;
import com.example.orders_service.common.handler.exception.CustomException;
import com.example.orders_service.orders.dto.request.OrderCreateRequestDto;
import com.example.orders_service.orders.dto.response.OrderCreateResponseDto;
import com.example.orders_service.orders.dto.response.OrderSoftDeleteResponseDto;
import com.example.orders_service.orders.entity.Orders;
import com.example.orders_service.orders.enums.OrderType;
import com.example.orders_service.orders.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private StockClient stockClient;

    @Nested
    @DisplayName("예약 상품 주문 가능 시간 확인 테스트")
    class CheckAvailableTimeTests {

        @Test
        @DisplayName("예약 상품 주문 가능 시간 내에 있을 때 예외가 발생하지 않아야 함")
        void checkAvailableTime_WithinAvailableTime_NoExceptionThrown() {
            // given
            LocalDateTime now = LocalDateTime.now();
            ProductDetailsResponseDto productDetails = new ProductDetailsResponseDto(
                    1L,
                    "제품명",
                    BigDecimal.valueOf(1000),
                    ProductType.RESERVED,
                    now.minusHours(1),
                    now.plusHours(1)
            );

            // when, then
            assertDoesNotThrow(() -> orderService.checkAvailableTime(productDetails));
        }

        @Test
        @DisplayName("예약 상품 주문 가능 시간 밖에 있을 때 예외가 발생해야 함")
        void checkAvailableTime_OutOfAvailableTime_ThrowsException() {
            // given
            LocalDateTime now = LocalDateTime.now();
            ProductDetailsResponseDto productDetails = new ProductDetailsResponseDto(
                    1L,
                    "제품명",
                    BigDecimal.valueOf(1000),
                    ProductType.RESERVED,
                    now.plusHours(1),
                    now.plusHours(2)
            );

            // when, then
            assertThatThrownBy(() -> orderService.checkAvailableTime(productDetails))
                    .isInstanceOf(CustomException.class);
        }

    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTests {

        @Test
        @DisplayName("주문 생성 성공")
        void createOrder_successfully() {
            // given
            Long userId = 1L;
            Long productId = 1L;
            BigDecimal price = BigDecimal.valueOf(10000);
            Long quantity = 100L;
            String productName = "새 상품";
            ProductType productType = ProductType.RESERVED;
            LocalDateTime availableFrom = LocalDateTime.now().minusHours(1);
            LocalDateTime availableUntil = LocalDateTime.now().plusHours(1);

            OrderCreateRequestDto requestDto =
                    new OrderCreateRequestDto(userId, productId, price, quantity);
            ProductDetailsResponseDto productDetails =
                    new ProductDetailsResponseDto(productId, productName, price,
                            productType, availableFrom, availableUntil);
            StockResponseDto stockResponse = new StockResponseDto(productId, 100L);

            Orders order = Orders.builder()
                    .userId(userId)
                    .productId(productId)
                    .price(price)
                    .quantity(quantity)
                    .ordersType(OrderType.INITIATED)
                    .build();

            // mocking
            when(productClient.getProductDetails(productId)).thenReturn(productDetails);
            when(stockClient.getProductStocks(productId)).thenReturn(stockResponse);

            // when
            OrderCreateResponseDto responseDto = orderService.createOrder(requestDto);

            // then
            assertNotNull(responseDto);
            assertEquals(userId, responseDto.userId());
            assertEquals(productId, responseDto.productId());
            assertEquals(price, responseDto.price());
            assertEquals(quantity, responseDto.quantity());
            assertNotNull(responseDto.ordersType());

            verify(productClient, times(1)).getProductDetails(productId);
            verify(stockClient, times(1)).getProductStocks(productId);
            verify(stockClient, times(1)).decreaseProductStock(
                    new StockRequestDto(productId, quantity));
            verify(orderRepository, times(1)).save(any(Orders.class));

        }

        @Test
        @DisplayName("주문 생성 전 상품이 존재하지 않을 때 커스텀 예외 발생")
        void givenNonExistentProduct_whenCreateOrder_thenThrowsException() {
            // given
            Long userId = 1L;
            Long productId = 1L;
            BigDecimal price = BigDecimal.valueOf(10000);
            Long quantity = 100L;

            OrderCreateRequestDto requestDto =
                    new OrderCreateRequestDto(userId, productId, price, quantity);

            // mocking
            when(productClient.getProductDetails(productId)).thenReturn(null);

            // when, then
            assertThatThrownBy(() -> orderService.createOrder(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("요청한 상품이 존재하지 않습니다");

        }

        @Test
        @DisplayName("예약 상품이지만 구매 가능 시간이 아닐 때 커스텀 예외 발생")
        void givenReservedProductOutOfAvailableTime_whenCreateOrder_thenThrowsException() {
            // given
            Long productId = 1L;
            ProductDetailsResponseDto productDetails = new ProductDetailsResponseDto(
                    productId, "새 상품", BigDecimal.valueOf(500), ProductType.RESERVED,
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1)
            );

            OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                    1L, productId, BigDecimal.valueOf(10000), 10L);

            // mocking
            when(productClient.getProductDetails(productId)).thenReturn(productDetails);

            // when, then
            assertThatThrownBy(() -> orderService.createOrder(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("구매 가능 시간이 아닙니다");
        }

        @Test
        @DisplayName("요청된 가격과 상품의 실제 가격이 다를 때 커스텀 예외 발생")
        void givenPriceMismatch_whenCreateOrder_thenThrowsException() {
            // given
            Long productId = 1L;
            BigDecimal requestPrice = BigDecimal.valueOf(9999);
            ProductDetailsResponseDto productDetails = new ProductDetailsResponseDto(
                    productId, "새 상품", BigDecimal.valueOf(500), ProductType.RESERVED,
                    LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)
            );

            OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                    1L, productId, requestPrice, 10L);

            // mocking
            when(productClient.getProductDetails(productId)).thenReturn(productDetails);

            // when, then
            assertThatThrownBy(() -> orderService.createOrder(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("가격이 일치하지 않습니다");
        }
    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class GetOrderDetailsTests {

        @Test
        @DisplayName("주문 조회")
        void givenPriceMismatch_whenCreateOrder_thenThrowsException() {
            // given
            Long orderId = 1L;
            Orders orders = Orders.builder()
                    .id(1L)
                    .userId(1L)
                    .productId(1L)
                    .price(BigDecimal.valueOf(500))
                    .quantity(2L)
                    .ordersType(OrderType.IN_PROGRESS)
                    .build();

            // mocking
            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));

            // when
            OrderDetailsResponseDto responseDto = orderService.getOrderDetails(orderId);

            // then
            assertEquals(orderId, responseDto.orderId());
            assertEquals(orders.getUserId(), responseDto.userId());
            assertEquals(orders.getProductId(), responseDto.productId());
            assertEquals(orders.getPrice(), responseDto.price());
            assertEquals(orders.getQuantity(), responseDto.quantity());
            assertEquals(orders.getOrdersType(), responseDto.ordersType());

        }

        @Test
        @DisplayName("주문 조회시 주문을 찾을 수 없는 경우 커스텀 예외 발생")
        void givenNonexistentOrder_whenGetOrderDetails_thenThrowsException() {
            // given
            Long orderId = 1L;

            // mocking
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> orderService.getOrderDetails(orderId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("주문이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("주문 취소 테스트")
    class SoftDeleteOrderTests {

        @Test
        @DisplayName("주문 취소 성공")
        void givenOrderExistsAndCancelable_whenSoftDeleteOrder_thenSuccess() {
            // given
            Long orderId = 1L;
            Orders orders = Orders.builder()
                    .id(orderId)
                    .userId(1L)
                    .productId(1L)
                    .price(BigDecimal.valueOf(500))
                    .quantity(5L)
                    .ordersType(OrderType.IN_PROGRESS)
                    .build();

            // mocking
            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));

            ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);

            // when
            OrderSoftDeleteResponseDto response = orderService.softDeleteOrder(orderId);

            // then
            assertEquals(OrderType.CANCEL, response.ordersType());
            verify(stockClient, times(1)).increaseProductStock(any(StockRequestDto.class));
            verify(orderRepository, times(1)).save(orderCaptor.capture());
            Orders capturedOrder = orderCaptor.getValue();
            assertEquals(OrderType.CANCEL, capturedOrder.getOrdersType());
        }

        @Test
        @DisplayName("존재하지 않는 주문 취소 시 커스텀 예외 발생")
        void givenNonexistentOrder_whenSoftDeleteOrder_thenThrowsOrderNotFoundException() {
            // given
            Long nonexistentOrderId = 1L;

            // mocking
            when(orderRepository.findById(nonexistentOrderId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> orderService.softDeleteOrder(nonexistentOrderId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("주문이 존재하지 않습니다.");

        }

        @Test
        @DisplayName("주문 상태가 이미 CANCEL일 경우 커스텀 예외 발생")
        void givenOrderAlreadyCancelled_whenSoftDeleteOrder_thenThrowsException() {
            // given
            Long orderId = 2L;
            Orders cancelledOrder = new Orders();
            cancelledOrder.updateOrderStatus(OrderType.CANCEL);
            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(cancelledOrder));

            // when & then
            assertThatThrownBy(() -> orderService.softDeleteOrder(orderId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("이미 취소된 주문입니다.");
        }

        @Test
        @DisplayName("주문 상태가 FAILED_CUSTOMER일 경우 예외 발생")
        void givenOrderFailedCustomer_whenSoftDeleteOrder_thenThrowsException() {
            // given
            Long orderId = 3L;
            Orders failedCustomerOrder = new Orders();
            failedCustomerOrder.updateOrderStatus(OrderType.FAILED_CUSTOMER);
            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(failedCustomerOrder));

            // when & then
            assertThatThrownBy(() -> orderService.softDeleteOrder(orderId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("결제 실패한 주문입니다");
        }
    }

    @Nested
    @DisplayName("주문 상태 업데이트 테스트")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("주문 상태 업데이트 성공")
        void givenExistingOrder_whenUpdateOrderStatus_thenSuccess() {
            // given
            Long orderId = 1L;
            OrderStatusUpdateRequestDto statusUpdateRequest = new OrderStatusUpdateRequestDto(OrderType.COMPLETED);
            Orders orders = Orders.builder()
                    .id(orderId)
                    .userId(1L)
                    .productId(1L)
                    .price(BigDecimal.valueOf(500))
                    .quantity(5L)
                    .ordersType(OrderType.IN_PROGRESS)
                    .build();

            // mocking
            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));

            // when
            orderService.updateOrderStatus(orderId, statusUpdateRequest);

            // then
            assertEquals(OrderType.COMPLETED, orders.getOrdersType());
            verify(orderRepository).save(orders);
        }

        @Test
        @DisplayName("주문을 찾을 수 없을 때 예외 발생")
        void givenInvalidOrderId_whenUpdateOrderStatus_thenThrowOrderNotFoundException() {
            // given
            Long invalidOrderId = 999L;
            OrderStatusUpdateRequestDto requestDto = new OrderStatusUpdateRequestDto(OrderType.COMPLETED);

            when(orderRepository.findById(invalidOrderId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.updateOrderStatus(invalidOrderId, requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("주문이 존재하지 않습니다.");
        }
    }

}