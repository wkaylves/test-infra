package com.github.kaylves.test.infra.example.repository;

import com.github.kaylves.test.infra.example.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
