package com.pkkulhari.orderservice.repositories;

import com.pkkulhari.orderservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
