package com.devops.order;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping
    public String getOrders() {
        return "Order Service is running successfully!";
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {

        if (order.getOrderId() == null) {
            order.setOrderId(1001L);
        }

        return order;
    }
}

