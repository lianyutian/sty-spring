package com.sty.spring;

import com.sty.spring.context.ApplicationContext;

/**
 *
 *
 * @author lm
 * @since 2024-07-23 11:05:18
 * @version 1.0
 */
public class TestSpring {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(ApplicationConfig.class);
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        System.out.println(orderService);

        UserServiceInterface userService = (UserServiceInterface) applicationContext.getBean("userService");
        userService.testAop();
    }
}
