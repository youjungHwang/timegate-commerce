package com.example.orders_service.orders.entity;

import com.example.orders_service.common.entity.BaseTimeEntity;
import com.example.orders_service.common.handler.exception.CustomException;
import com.example.orders_service.common.handler.exception.ErrorCode;
import com.example.orders_service.orders.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType ordersType;

    private LocalDateTime deletedAt;

    /**
     * ordersType 변경
     */
    public void updateOrderStatus(OrderType newType) {
        if (newType == null || this.ordersType == newType) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }
        this.ordersType = newType;
        if (newType == OrderType.CANCEL) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}
