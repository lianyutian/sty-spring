package com.sty.spring.context;

import com.sty.spring.annotations.Autowire;
import com.sty.spring.annotations.Component;
import com.sty.spring.annotations.ComponentScan;
import com.sty.spring.annotations.Scope;
import com.sty.spring.aware.BeanNameAware;
import com.sty.spring.bean.BeanDefinition;
import com.sty.spring.bean.BeanPostProcessor;
import com.sty.spring.bean.InitializingBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bean容器
 *
 * @author lm
 * @since 2024-07-23 11:06:56
 * @version 1.0
 */
public class ApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    private Class configClass;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String, Object> singletonObjects = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 扫描bean
        scan(configClass);

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                Object singletonBean = createBean(beanDefinition);
                singletonObjects.put(beanName, singletonBean);
            }
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Object instance;
        try {
            Class clazz = beanDefinition.getType();
            // 根据构造器创建bean
            instance = clazz.getConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            // 属性注入
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowire.class)) {
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getName()));
                }
            }

            // 回调处理
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanDefinition.getBeanName());
            }

            // BeanPostProcessor前置处理
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanDefinition.getBeanName());
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // BeanPostProcessor后置处理
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanDefinition.getBeanName());
            }


        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            // 拿到包扫描路径
            String path = componentScanAnnotation.value();
            // 转换为路径
            path = path.replace(".", "/");
            // 遍历包下文件找出@Component注解的类
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String absolutePath = f.getAbsolutePath();
                    if (absolutePath.endsWith(".class")) {
                        String className = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                        className = className.replace("\\", ".");
                        try {
                            // 加载类
                            Class<?> clazz = classLoader.loadClass(className);
                            if (clazz.isAnnotationPresent(Component.class)) {
                                // 用于判断传入的bean是否是BeanPostProcessor的子类
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    beanPostProcessorList.add((BeanPostProcessor) clazz.getConstructor().newInstance());
                                }

                                Component component = clazz.getAnnotation(Component.class);
                                String beanName = component.value();
                                if (StringUtils.isBlank(beanName)) {
                                    // 获取类名
                                    beanName = StringUtils.uncapitalize(clazz.getSimpleName());
                                }
                                BeanDefinition beanDefinition = new BeanDefinition();
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scope = clazz.getAnnotation(Scope.class);
                                    String scopeValue = scope.value();
                                    // 默认单例
                                    if (StringUtils.isBlank(scopeValue)) {
                                        beanDefinition.setScope("singleton");
                                    } else { // 多例
                                        beanDefinition.setScope(scopeValue);
                                    }
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinition.setType(clazz);
                                beanDefinition.setBeanName(beanName);
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        }


    }


    public Object getBean(String beanName) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException("bean定义不存在");
        } else {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                // 属性注入时这里可能存在为null的情况
                // 例如在UserService注入OrderService时，如果先去初始化UserService在注入OrderService时，此时OrderService为null
                if (singletonObjects.get(beanName) == null) {
                    return createBean(beanDefinition);
                }
                return singletonObjects.get(beanName);
            } else {
                return createBean(beanDefinition);
            }
        }
    }
}
