package com.sty.spring.bean;

import java.lang.reflect.InvocationTargetException;

/**
 * bean后置处理
 *
 * @author lm
 * @since 2024-07-23 16:12:56
 * @version 1.0
 */
public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return bean;
    }
}
