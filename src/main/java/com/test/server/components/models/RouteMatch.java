package com.test.server.components.models;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Getter
@Setter
public class RouteMatch {

    public final Method method;
    public final Object controller;
    public final Pattern pattern;

    public RouteMatch(Method method, Object controller, Pattern pattern) {
        this.method = method;
        this.controller = controller;
        this.pattern = pattern;
    }
}
