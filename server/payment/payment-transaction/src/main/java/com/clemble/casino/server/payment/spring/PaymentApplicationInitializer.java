package com.clemble.casino.server.payment.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.clemble.casino.server.spring.AbstractWebApplicationInitializer;

public class PaymentApplicationInitializer extends AbstractWebApplicationInitializer {

    @Override
    protected void doInit(ServletContext container) throws ServletException {
        // Step 2. Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(PaymentWebSpringConfiguration.class);
        // Step 3. Registering appropriate Dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("payment-transaction", new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
