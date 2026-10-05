package com.devops.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

   @Test
   void  userServiceMessageSHouldBeCorrect() {
        
       UserController controller = new UserController();

       String response = controller.getUsers();

       assertEquals(
               "User Service is running successfully!",
                response
                );
             }
}
