package com.clemble.casino.server.spring.common;

import java.io.IOException;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.server.player.notification.RabbitSystemNotificationService;
import com.clemble.casino.server.player.notification.RabbitSystemNotificationServiceListener;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.player.notification.SystemNotificationServiceListener;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import({ RabbitSpringConfiguration.class })
public class SystemNotificationSpringConfiguration implements SpringConfiguration {

    @Bean
    public SystemNotificationService systemNotificationService(
        @Value("${clemble.service.notification.user}") String user,
        @Value("${clemble.service.notification.password}") String password,
        @Value("${clemble.service.notification.host}") String host,
        Jackson2JsonMessageConverter jsonMessageConverter) throws IOException {
        return new RabbitSystemNotificationService(host, user, password, jsonMessageConverter);
    }

    @Bean(destroyMethod = "close")
    public SystemNotificationServiceListener presenceListenerService(ObjectMapper mapper, @Value("${clemble.service.notification.host}") String host) {
        return new RabbitSystemNotificationServiceListener(host, mapper);
    }

}
