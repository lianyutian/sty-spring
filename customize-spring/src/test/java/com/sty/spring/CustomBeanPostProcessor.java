package com.sty.spring;

import com.sty.spring.annotations.Component;
import com.sty.spring.bean.BeanPostProcessor;

/**
 * 自定义Bean处理器
 *
 * @author lm
 * @since 2024-07-23 16:15:04
 * @version 1.0
 */
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("postProcessBeforeInitialization " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("postProcessAfterInitialization " + beanName);
        }
        return bean;
    }
}
