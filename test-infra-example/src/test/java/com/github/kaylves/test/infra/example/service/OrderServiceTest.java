package com.github.kaylves.test.infra.example.service;

import com.github.kaylves.test.infra.example.model.Order;
import com.github.kaylves.test.infra.example.repository.OrderRepository;
import com.github.kaylves.test.infra.junit5.BaseJUnit5Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest extends BaseJUnit5Test {

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

    @Test
    @DisplayName("should verify multiple fields with soft assertions")
    void shouldVerifyMultipleFieldsSoftly() {
        Order order = new Order();
        order.setOrderNo("ORD-002");
        order.setCustomerName("Bob");
        order.setAmount(199.9);

        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.createOrder(order);
        softly.assertThat(result.getOrderNo()).isEqualTo("ORD-002");
        softly.assertThat(result.getCustomerName()).isEqualTo("Bob");
        softly.assertThat(result.getAmount()).isEqualTo(199.9);
    }
}
