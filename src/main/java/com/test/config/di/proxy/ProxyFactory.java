package com.test.config.di.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Predicate;

public class ProxyFactory {

    public static class MethodInvocation {
        private final Object target;
        private final Method method;
        private final Object[] args;

        public MethodInvocation(Object target, Method method, Object[] args) {
            this.target = target;
            this.method = method;
            this.args = args;
        }

        public Object proceed() throws Exception {
            return method.invoke(target, args);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, MethodInterceptor interceptor, Predicate<Method> methodPredicate) {
        Class<?>[] interfaces = target.getClass().getInterfaces();

        InvocationHandler handler = (proxy, method, args) -> {
            // Check if method should be intercepted
            if (methodPredicate.test(method)) {
                // Create method invocation context
                MethodInvocation invocation = new MethodInvocation(target, method, args);
                // Apply interceptor
                return interceptor.intercept(target, method, args, invocation);
            }
            // Direct invocation if no interception needed
            return method.invoke(target, args);
        };

        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                handler
        );
    }
}
