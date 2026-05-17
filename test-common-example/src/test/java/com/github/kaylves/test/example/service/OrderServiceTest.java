package com.github.kaylves.test.example.service;

import com.github.kaylves.test.example.model.Order;
import com.github.kaylves.test.example.repository.OrderRepository;
import com.github.kaylves.test.spring.mvc.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceTest extends BaseServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("should create order successfully")
    void shouldCreateOrder() {
        Order order = new Order();
        order.setOrderNo("ORD-001");
        order.setCustomerName("Alice");
        order.setAmount(99.9);

        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.createOrder(order);
        assertThat(result.getOrderNo()).isEqualTo("ORD-001");
        assertThat(result.getCustomerName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("should find order by id")
    void shouldFindOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD-001");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.findOrder(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getOrderNo()).isEqualTo("ORD-001");
    }
}
