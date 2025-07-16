package com.test;

import com.test.config.di.DIContainer;
import com.test.controller.UserInfoController;
import com.test.handler.UserInfoHandler;
import com.test.repository.impl.UserInfoRepository;
import com.test.server.Server;
import com.test.server.components.DispatcherServlet;
import com.test.server.components.RouteRegistry;
import com.test.server.utils.RequestResolver;
import com.test.util.mappers.UserInfoDataMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class ApplicationStart {

    public static void run(String[] args) {
        log.info("Starting Application. Args: {}", Arrays.asList(args));
        log.info("Creating DIContainer");
        DIContainer diContainer = new DIContainer();
        log.info("DIContainer created");
        log.info("Initializing DIContainer");
        initContainer(diContainer);
        try {
            Server s = (Server) diContainer.getInstance("Server");
            s.run();
        } catch (Exception e) {
            log.error("{}: {}", ApplicationStart.class.getSimpleName(), e.getMessage());
        }
    }

    private static void initContainer(DIContainer diContainer) {
        diContainer.addGlobalInterceptor(((target, method, args1, methodInvocation) -> {
            log.info("Method called: {}", method.getName());
            try {
                return methodInvocation.proceed();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new IllegalArgumentException("Cant invoke method " + e.getMessage());
            }
        }));

        // should be auto-registered
//        diContainer.registerComponent(ObjectMapper.class);
        diContainer.registerComponent(RequestResolver.class);
        diContainer.registerComponent(DispatcherServlet.class);
        diContainer.registerComponent(UserInfoDataMapper.class);
        diContainer.registerComponent(UserInfoRepository.class);
        diContainer.registerComponent(UserInfoHandler.class);
        diContainer.registerComponent(UserInfoController.class);
        diContainer.registerComponent(Server.class);

        try {
            RouteRegistry.registerController(diContainer.getInstance("UserInfoController"));
        } catch (Exception e) {
            log.error("Error registering [UserInfoController] controller. Message: {}", e.getMessage());
        }

        log.info("DIContainer initialized");
    }
}
