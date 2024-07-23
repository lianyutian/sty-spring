package com.sty.spring;

import com.sty.spring.annotations.Component;
import com.sty.spring.bean.BeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 *
 *
 * @author lm
 * @since 2024-07-23 16:49:28
 * @version 1.0
 */
@Component
public class AopBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AopBeanPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (beanName.equals("userService")) {
            // 创建动态代理对象
            return Proxy.newProxyInstance(
                    UserServiceInterface.class.getClassLoader(),
                    new Class[]{UserServiceInterface.class},
                    (proxy, method, args) -> {
                        System.out.println("before aop");
                        method.invoke(bean, args);
                        System.out.println("after aop");
                        return proxy;
                    }
            );
        }
        return bean;
    }
}

