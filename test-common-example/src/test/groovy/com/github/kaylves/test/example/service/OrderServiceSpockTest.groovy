package com.github.kaylves.test.example.service

import com.github.kaylves.test.example.model.Order
import com.github.kaylves.test.example.repository.OrderRepository
import com.github.kaylves.test.spock.BaseSpockSpec
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.when

class OrderServiceSpockTest extends BaseSpockSpec {

    @Mock
    OrderRepository orderRepository

    @InjectMocks
    OrderService orderService

    def setup() {
        MockitoAnnotations.openMocks(this)
    }

    def "should create order"() {
        given:
        def order = new Order()
        order.setOrderNo("ORD-001")
        order.setCustomerName("Alice")
        order.setAmount(99.9)

        when(orderRepository.save(any())).thenReturn(order)

        when:
        def result = orderService.createOrder(order)

        then:
        result.getOrderNo() == "ORD-001"
        result.getCustomerName() == "Alice"
    }

    def "should find order by id"() {
        given:
        def order = new Order()
        order.setId(1L)
        order.setOrderNo("ORD-001")
        order.setCustomerName("Alice")
        order.setAmount(99.9)

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order))

        when:
        def result = orderService.findOrder(1L)

        then:
        result.isPresent()
        result.get().getOrderNo() == "ORD-001"
        result.get().getCustomerName() == "Alice"
    }

    def "should return empty optional when order not found"() {
        given:
        when(orderRepository.findById(999L)).thenReturn(Optional.empty())

        when:
        def result = orderService.findOrder(999L)

        then:
        !result.isPresent()
    }
}
