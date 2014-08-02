package com.clemble.casino.server.profile.spring;

import com.clemble.casino.server.spring.AbstractWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by mavarazy on 7/4/14.
 */
public class PlayerProfileApplicationInitializer extends AbstractWebApplicationInitializer {

    @Override
    protected void doInit(ServletContext container) throws ServletException {
        // Step 2. Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(PlayerProfileWebSpringConfiguration.class);
        // Step 3. Registering appropriate Dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("player-profile", new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}
