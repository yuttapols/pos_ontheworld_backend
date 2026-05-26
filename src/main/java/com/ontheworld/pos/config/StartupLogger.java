package com.ontheworld.pos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    private final Environment env;

    public StartupLogger(Environment env) {
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String swaggerPath = env.getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");

        log.info("==============================================");
        log.info("  POS OnTheWorld Backend started successfully");
        log.info("  Swagger UI  : http://localhost:{}{}{}", port, contextPath, swaggerPath);
        log.info("  API Docs    : http://localhost:{}{}/api-docs", port, contextPath);
        log.info("==============================================");
    }
}
