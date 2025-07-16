package com.test.server;

import com.test.annotations.di.Component;
import com.test.annotations.di.Inject;
import com.test.server.components.DispatcherServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
public class Server {

    private static final Tomcat tomcat = new Tomcat();

    @Inject
    private DispatcherServlet dispatcherServlet;

    public Server() {
        tomcat.setPort(8989);
        Connector connector = tomcat.getConnector();

        connector.setProperty("maxThreads", "200"); // Maximum number of threads
        connector.setProperty("minSpareThreads", "20"); // Minimum number of idle threads
        connector.setProperty("acceptCount", "100");

        tomcat.setConnector(connector);
        tomcat.setBaseDir("temp");
    }

    public void run() {
        log.info("Starting Tomcat server. [{}]", LocalDateTime.now());

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        if (Objects.isNull(dispatcherServlet))
            throw new NullPointerException("DispatcherServlet is not provided!");

        Tomcat.addServlet(context, "DispatcherServlet", dispatcherServlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "DispatcherServlet");

        try {
            tomcat.start();
            tomcat.getServer().await();
            log.info("Tomcat is started and running");
        } catch (Exception e) {
            log.error("Error starting Tomcat", e);
        }
    }

    public void stop() {
        synchronized (tomcat) {
                try {
                    tomcat.stop();
                    tomcat.destroy();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        }
    }
}
