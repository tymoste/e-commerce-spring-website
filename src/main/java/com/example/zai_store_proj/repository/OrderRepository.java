package com.example.zai_store_proj.repository;

import com.example.zai_store_proj.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}