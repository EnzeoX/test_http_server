package com.test.server.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.annotations.di.Component;
import com.test.annotations.di.Inject;
import com.test.annotations.server.PathVariable;
import com.test.annotations.server.RequestData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestResolver {

    // This should be auto-injected too, but it's conflicts with custom DI
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public Object[] resolveMethodArguments(Method method, Map<String, String> pathVariables, HttpServletRequest req) {
        Parameter[] params = method.getParameters();
        Object[] values = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (param.isAnnotationPresent(PathVariable.class)) {
                String name = param.getAnnotation(PathVariable.class).value();
                String val = pathVariables.get(name);
                values[i] = castParam(param.getType(), val);
            } else if (param.isAnnotationPresent(RequestData.class)) {
                try {
                    String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

                    Class<?> rawType = param.getType();
                    Type genericType = param.getParameterizedType();

                    if (Collection.class.isAssignableFrom(rawType) && genericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) genericType;
                        Type actualType = pt.getActualTypeArguments()[0];

                        JavaType collectionType = objectMapper.getTypeFactory()
                                .constructCollectionType((Class<? extends Collection<?>>) rawType, (Class<?>) actualType);

                        values[i] = objectMapper.readValue(body, collectionType);
                    } else {
                        values[i] = objectMapper.readValue(body, param.getType());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IllegalArgumentException("Provided data cannot be parsed to POJO");
                }
            }
        }

        return values;
    }

    private Object castParam(Class<?> type, String val) {
        if (val == null) return null;
        if (type == String.class) return val;
        if (type == Integer.class || type == int.class) return Integer.parseInt(val);
        if (type == Long.class || type == long.class) return Long.parseLong(val);
        throw new IllegalArgumentException("Unsupported param type: " + type);
    }
}
