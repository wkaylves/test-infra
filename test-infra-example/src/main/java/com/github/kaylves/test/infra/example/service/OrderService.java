package com.github.kaylves.test.infra.example.service;

import com.github.kaylves.test.infra.example.model.Order;
import com.github.kaylves.test.infra.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findOrder(Long id) {
        return orderRepository.findById(id);
    }
}
