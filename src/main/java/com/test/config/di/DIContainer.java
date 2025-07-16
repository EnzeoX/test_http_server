package com.test.config.di;

import com.test.annotations.di.Component;
import com.test.annotations.di.Inject;
import com.test.config.di.proxy.MethodInterceptor;
import com.test.config.di.proxy.ProxyFactory;
import com.test.config.di.proxy.annotations.Intercept;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DIContainer {

    private final Map<String, Object> components;
    private final Map<String, Class<?>> componentClasses;
    private final List<MethodInterceptor> globalInterceptors;

    public DIContainer() {
        this.components = new ConcurrentHashMap<>();
        this.componentClasses = new ConcurrentHashMap<>();
        this.globalInterceptors = new ArrayList<>();
    }

    public void addGlobalInterceptor(MethodInterceptor methodInterceptor) {
        this.globalInterceptors.add(methodInterceptor);
    }

    public void registerComponent(Class<?> component) {
        Component annotation = component.getAnnotation(Component.class);
        if (Objects.nonNull(annotation)) {
            String componentName = annotation.value().isEmpty()
                    ? component.getSimpleName()
                    : annotation.value();

            componentClasses.put(componentName, component);
        } else {
            log.warn("[{}] component doesn't have annotation", component.getSimpleName());
            componentClasses.put(component.getSimpleName(), component);
        }
    }

    public Object getInstance(String name) throws Exception {
        Object instance = components.get(name);
        if (Objects.nonNull(instance))
            return instance;

        Class<?> componentClass = componentClasses.get(name);
        if (Objects.isNull(componentClass))
            throw new IllegalArgumentException("No component registered with name " + name);

        instance = createInstance(componentClass);
        components.put(name, componentClass);

        return instance;
    }

    private Object createInstance(Class<?> componentClass) throws Exception {
        Object instance = componentClass.getDeclaredConstructor().newInstance();

        for (Field f : componentClass.getDeclaredFields()) {
            Inject annotation = f.getAnnotation(Inject.class);

            if (Objects.nonNull(annotation)) {
                String dependencyName = annotation.value().isEmpty()
                        ? f.getType().getSimpleName()
                        : annotation.value();
                f.setAccessible(true);
                Object dependency = getInstance(dependencyName);
                f.set(instance, dependency);
            }
        }

        if (!globalInterceptors.isEmpty() && instance.getClass().getInterfaces().length > 0) {
            MethodInterceptor compositeInterceptor = (target, method, args, invocation) -> {
                Object currentTarget = target;
                for (MethodInterceptor interceptor : globalInterceptors) {
                    currentTarget = interceptor.intercept(currentTarget, method, args, invocation);
                }
                return currentTarget;
            };
            return ProxyFactory.createProxy(
                    instance,
                    compositeInterceptor,
                    method -> method.isAnnotationPresent(Intercept.class)
            );
        }

        return instance;
    }
}
