package com.example.orders_service.orders.repository;

import com.example.orders_service.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
