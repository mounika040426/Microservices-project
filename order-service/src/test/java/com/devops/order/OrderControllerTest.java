package com.devops.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderControllerTest {

    @Test
    void getOrdersShouldReturnCorrectMessage() {

        OrderController controller = new OrderController();

        String response = controller.getOrders();

        assertEquals(
                "Order Service is running successfully!",
                response
        );
    }

    @Test
    void createOrderShouldAssignOrderId() {

        OrderController controller = new OrderController();

        Order order = new Order(
                null,
                101L,
                501L,
                2
        );

        Order response = controller.createOrder(order);

        assertEquals(1001L, response.getOrderId());
        assertEquals(101L, response.getUserId());
        assertEquals(501L, response.getProductId());
        assertEquals(2, response.getQuantity());
    }
}
