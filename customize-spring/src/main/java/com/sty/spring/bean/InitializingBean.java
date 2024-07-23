package com.sty.spring.bean;

/**
 * Bean初始化
 *
 * @author lm
 * @since 2024-07-23 16:10:22
 * @version 1.0
 */
public interface InitializingBean {
    void afterPropertiesSet();
}
