package com.devops.product;

import org.junit.jupiter.api.Test;

import static
org.junit.jupiter.api.Assertions.assertEquals;

class ProductControllerTest {
 
    @Test
    void
productServiceMessageShouldBeCorrect() {

        ProductController controller = new ProductController();

        String response = controller.getProducts();

        assertEquals("Product Service is running successfully!",
                      response
                );
           }
}
