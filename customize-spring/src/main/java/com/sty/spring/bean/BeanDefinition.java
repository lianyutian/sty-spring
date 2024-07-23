package com.sty.spring.bean;

/**
 * bean定义
 *
 * @author lm
 * @since 2024-07-23 13:37:00
 * @version 1.0
 */
public class BeanDefinition {
    private Class type;
    private String scope;
    private String beanName;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
