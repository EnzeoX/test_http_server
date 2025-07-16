package com.test.server.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.annotations.di.Component;
import com.test.annotations.di.Inject;
import com.test.server.components.models.RouteMatch;
import com.test.server.utils.RequestResolver;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class DispatcherServlet extends HttpServlet {

    private final static int REQUEST_TIMEOUT = 30 * 1000;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // @Inject
    // This should be auto-injected too, but it's conflicts with custom DI
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private RequestResolver requestResolver;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getRequestURI();
        String method = req.getMethod();

        Map<String, String> pathVariables = new HashMap<>();

        RouteMatch match = RouteRegistry.findRoute(method, path, pathVariables);

        if (Objects.isNull(match)) {
            resp.setStatus(404);
            resp.getWriter().write(String.format("Path [%s] not found", path));
            return;
        }

        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(REQUEST_TIMEOUT);

        executorService.submit(() -> {
            try {
                Object[] args = requestResolver.resolveMethodArguments(match.method, pathVariables, req);
                Object result = match.method.invoke(match.controller, args);

                HttpServletResponse asyncResp = (HttpServletResponse) asyncContext.getResponse();
                asyncResp.setContentType("application/json");
                asyncResp.setStatus(200);
                asyncResp.getWriter().write(objectMapper.writeValueAsString(result));
            } catch (Exception e) {
                try {
                    HttpServletResponse asyncResp = (HttpServletResponse) asyncContext.getResponse();
                    asyncResp.setStatus(500);
                    asyncResp.getWriter().write("Internal Error: " + e.getMessage());
                } catch (IOException ioException) {
                    log.error("Request processing error: {}", e.getMessage());
                }
            } finally {
                asyncContext.complete();
            }
        });
    }


}
