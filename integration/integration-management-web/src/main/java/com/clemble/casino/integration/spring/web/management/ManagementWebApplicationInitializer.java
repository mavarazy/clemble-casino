package com.clemble.casino.integration.spring.web.management;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.clemble.casino.server.spring.web.BasicWebApplicationInitializer;

public class ManagementWebApplicationInitializer extends BasicWebApplicationInitializer {

    @Override
    protected void doInit(ServletContext container) throws ServletException {
        // Step 1. Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ManagementWebSpringConfiguration.class);
        // Step 2. Registering appropriate Dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("integration-management", new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}
