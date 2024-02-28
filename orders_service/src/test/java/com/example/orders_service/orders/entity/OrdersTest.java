package com.example.orders_service.orders.entity;

import com.example.orders_service.common.handler.exception.CustomException;
import com.example.orders_service.orders.enums.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("주문 엔티티 기능 테스트")
class OrdersTest {
    private Orders orders;

    @BeforeEach
    void setUp() {
        orders = Orders.builder()
                .id(1L)
                .userId(1L)
                .productId(1L)
                .price(BigDecimal.valueOf(10000))
                .quantity(1L)
                .ordersType(OrderType.INITIATED)
                .build();
    }

    @DisplayName("주문 취소 시 deletedAt 필드가 현재 시간으로 설정되는지 확인")
    @Test
    void updateOrderStatusToCancel() {
        // given

        // when
        orders.updateOrderStatus(OrderType.CANCEL);

        // then
        assertThat(orders.getDeletedAt()).isNotNull();

        long secondsDifference = ChronoUnit.SECONDS.between(orders.getDeletedAt(), LocalDateTime.now());
        assertThat(secondsDifference).isLessThanOrEqualTo(5);
    }

    @DisplayName("주문 상태를 진행 중(IN_PROGRESS)으로 변경할 때 ordersType 필드가 정상적으로 업데이트 되는지 확인")
    @Test
    void updateOrderStatusToInProgress() {
        // given

        // when
        orders.updateOrderStatus(OrderType.IN_PROGRESS);

        // then
        assertThat(orders.getOrdersType()).isEqualTo(OrderType.IN_PROGRESS);
        assertThat(orders.getDeletedAt()).isNull();
    }

    @DisplayName("생성자와 게터 메소드가 올바르게 동작하는지 검증")
    @Test
    void constructorAndGetterVerification() {
        // given

        // then
        assertThat(orders.getId()).isEqualTo(1L);
        assertThat(orders.getUserId()).isEqualTo(1L);
        assertThat(orders.getProductId()).isEqualTo(1L);
        assertThat(orders.getPrice()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(orders.getQuantity()).isEqualTo(1L);
        assertThat(orders.getOrdersType()).isEqualTo(OrderType.INITIATED);
    }

    @DisplayName("유효하지 않은 주문 상태 변경 시 예외가 발생하는지 확인")
    @Test
    void updateOrderWithInvalidData() {
        // given
        OrderType targetOrderStatus = orders.getOrdersType();

        // then
        assertThatThrownBy(() -> orders.updateOrderStatus(targetOrderStatus))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("유효하지 않은 주문 상태 변경입니다.");
    }
}