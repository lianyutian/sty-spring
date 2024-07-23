package com.sty.spring;

import com.sty.spring.annotations.Autowire;
import com.sty.spring.annotations.Component;
import com.sty.spring.aware.BeanNameAware;
import com.sty.spring.bean.InitializingBean;

/**
 *
 *
 * @author lm
 * @since 2024-07-23 11:05:04
 * @version 1.0
 */
@Component
public class UserService implements InitializingBean, UserServiceInterface, BeanNameAware {
    @Autowire
    private OrderService orderService;

    public void print() {
        System.out.println(orderService);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("初始化");
    }

    @Override
    public void testAop() {
        System.out.println("testAop");
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println(beanName);
    }
}
