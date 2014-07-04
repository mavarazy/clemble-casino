package com.clemble.casino.server.spring.web.management;

import java.security.NoSuchAlgorithmException;

import com.clemble.casino.server.configuration.SimpleNotificationConfigurationService;
import com.clemble.casino.server.configuration.SimpleResourceLocationService;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.web.OAuthSpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.configuration.ResourceLocationService;
import com.clemble.casino.configuration.ServerRegistryConfiguration;
import com.clemble.casino.error.ClembleCasinoValidationService;
import com.clemble.casino.server.player.presence.ServerPlayerPresenceService;
import com.clemble.casino.server.player.presence.SystemNotificationService;
import com.clemble.casino.server.player.registration.ServerProfileRegistrationService;
import com.clemble.casino.server.repository.player.PlayerSessionRepository;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.server.spring.web.WebCommonSpringConfiguration;
import com.clemble.casino.server.web.management.PlayerSessionController;

@Configuration
@Import(value = {
        WebCommonSpringConfiguration.class,
        OAuthSpringConfiguration.class
})
public class ManagementWebSpringConfiguration implements SpringConfiguration {

    @Autowired
    public ServerRegistryConfiguration paymentEndpointRegistry;

    @Bean
    public PlayerSessionController playerSessionController(
            ResourceLocationService resourceLocationService,
            PlayerSessionRepository playerSessionRepository,
            ServerPlayerPresenceService playerStateManager) {
        return new PlayerSessionController(resourceLocationService, playerSessionRepository, playerStateManager);
    }

    @Bean
    public ServerRegistryConfiguration serverRegistryConfiguration(
        @Value("${clemble.management.configuration.notification}") String notificationBase,
        @Value("${clemble.management.configuration.player}") String playerBase,
        @Value("${clemble.management.configuration.payment}") String paymentBase,
        @Value("${clemble.management.configuration.game}") String gameBase) {
        return new ServerRegistryConfiguration(notificationBase, playerBase, paymentBase, gameBase);
    }

    @Bean
    public ResourceLocationService resourceLocationService(ServerRegistryConfiguration serverRegistryConfiguration) {
        SimpleNotificationConfigurationService configurationService = new SimpleNotificationConfigurationService("guest", "guest", serverRegistryConfiguration.getPlayerNotificationRegistry());
        return new SimpleResourceLocationService(configurationService, serverRegistryConfiguration);
    }

}
