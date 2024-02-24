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

    public void createOrders(OrdersCreateRequestDto requestDto) {
        this.userId = requestDto.userId();
        this.productId = requestDto.productId();
        this.price = requestDto.price();
        this.quantity = requestDto.quantity();
    }
    public OrdersCreateResponseDto toCreateResponseDto() {
        return new OrdersCreateResponseDto(
                this.getId(),
                this.getUserId(),
                this.getProductId(),
                this.getPrice(),
                this.getQuantity(),
                this.getOrdersType()
        );
    }




}
