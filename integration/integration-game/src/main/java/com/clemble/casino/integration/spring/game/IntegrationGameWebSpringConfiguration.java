package com.clemble.casino.integration.spring.game;

import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.server.spring.web.WebCommonSpringConfiguration;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by mavarazy on 7/5/14.
 */
@Configuration
@Import({WebCommonSpringConfiguration.class, IntegrationGameSpringConfiguration.class})
public class IntegrationGameWebSpringConfiguration implements SpringConfiguration {
}
