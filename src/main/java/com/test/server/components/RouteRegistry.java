package com.test.server.components;

import com.test.annotations.server.GetMapping;
import com.test.annotations.server.PutMapping;
import com.test.annotations.server.RequestMapping;
import com.test.server.components.models.RouteMatch;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RouteRegistry {

    private static final Map<String, List<RouteMatch>> routeMap = new HashMap<>();

    public static void registerController(Object controller) {
        Class<?> clazz = controller.getClass();

        String basePath = "";
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            basePath = clazz.getAnnotation(RequestMapping.class).path();
        }

        for (Method method : clazz.getDeclaredMethods()) {
            String httpMethod = null;
            String subPath = null;

            if (method.isAnnotationPresent(GetMapping.class)) {
                httpMethod = "GET";
                subPath = method.getAnnotation(GetMapping.class).path();
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                httpMethod = "PUT";
                subPath = method.getAnnotation(PutMapping.class).path();
            }

            if (httpMethod != null && subPath != null) {
                String fullPath = basePath + subPath;
                Pattern pattern = Pattern.compile(pathToRegex(fullPath));
                routeMap.computeIfAbsent(httpMethod, k -> new ArrayList<>())
                        .add(new RouteMatch(method, controller, pattern));
            }
        }
    }

    public static RouteMatch findRoute(String method, String uri, Map<String, String> pathVariables) {
        List<RouteMatch> routes = routeMap.getOrDefault(method, List.of());
        for (RouteMatch route : routes) {
            Matcher matcher = route.pattern.matcher(uri);
            if (matcher.matches()) {
                for (String name : getNamedGroupNames(route.pattern)) {
                    pathVariables.put(name, matcher.group(name));
                }
                return route;
            }
        }
        return null;
    }

    private static String pathToRegex(String path) {
        return path.replaceAll("\\{([a-zA-Z0-9_]+)}", "(?<$1>[^/]+)");
    }

    private static List<String> getNamedGroupNames(Pattern pattern) {
        Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9_]*)>").matcher(pattern.pattern());
        List<String> groupNames = new ArrayList<>();
        while (m.find()) {
            groupNames.add(m.group(1));
        }
        return groupNames;
    }
}
