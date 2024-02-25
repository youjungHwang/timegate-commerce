package com.example.orders_service.orders.entity;

import com.example.orders_service.orders.dto.request.OrdersCreateRequestDto;
import com.example.orders_service.orders.dto.response.OrdersCreateResponseDto;
import com.example.orders_service.orders.enums.OrdersType;
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
public class Orders {
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
    private OrdersType ordersType;

    private LocalDateTime deletedAt;

    public void cancelOrder(OrdersType type) {
        this.ordersType = type;
    }

    /**
     * ordersType 변경
     */
    public void updateOrderStatus(OrdersType type) {
        this.ordersType = type;
        if (type == OrdersType.CANCEL) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}
