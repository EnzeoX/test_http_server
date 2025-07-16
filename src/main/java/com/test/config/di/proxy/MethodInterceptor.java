package com.test.config.di.proxy;

import java.lang.reflect.Method;

public interface MethodInterceptor {

    Object intercept(Object target, Method method, Object[] args, ProxyFactory.MethodInvocation methodInvocation);

}
