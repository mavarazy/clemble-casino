package com.clemble.casino.server.spring.game;

import com.clemble.casino.game.*;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.game.specification.RoundGameConfiguration;
import com.clemble.casino.game.specification.PotGameConfiguration;
import com.clemble.casino.game.specification.TournamentGameConfiguration;
import com.clemble.casino.server.game.action.GameManagerFactory;
import com.clemble.casino.server.game.action.GameStateFactory;
import com.clemble.casino.server.game.action.GameStateFactoryFacade;
import com.clemble.casino.server.game.aspect.RoundGameAspectFactory;
import com.clemble.casino.server.game.aspect.ServerGameAspectFactory;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.clemble.casino.server.repository.game.RoundGameRecordRepository;
import com.clemble.casino.server.repository.game.PotGameRecordRepository;
import com.clemble.casino.server.repository.game.ServerGameConfigurationRepository;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Ignore
@Configuration
@Import(GameManagementSpringConfiguration.class)
@SuppressWarnings({"rawtypes"})
public class SimpleGameSpringConfiguration {

    @Bean
    public GameManagerFactory gameProcessor(PotGameRecordRepository potRepository,
                                            GameStateFactoryFacade stateFactory,
                                            ServerGameAspectFactory<RoundGameConfiguration, RoundGameContext, RoundGameRecord> processorFactory,
                                            ServerGameAspectFactory<PotGameConfiguration, PotGameContext, PotGameRecord> potProcessorFactory,
                                            ServerGameAspectFactory<TournamentGameConfiguration, TournamentGameContext, TournamentGameRecord> tournamentProcessorFactory,
                                            RoundGameRecordRepository sessionRepository,
                                            ServerGameConfigurationRepository configurationRepository,
                                            @Qualifier("playerNotificationService") PlayerNotificationService notificationService) {
        return new GameManagerFactory(potRepository, stateFactory, processorFactory, potProcessorFactory, tournamentProcessorFactory, sessionRepository, configurationRepository, notificationService);
    }

    @Bean
    public ServerGameAspectFactory<RoundGameConfiguration, RoundGameContext, RoundGameRecord> processorFactory() {
        return new ServerGameAspectFactory<>(RoundGameAspectFactory.class);
    }

    @Bean
    public GameStateFactory fakeStateFactory() {
        return new GameStateFactory() {

            @Override
            public Game getGame() {
                return Game.num;
            }

            @Override
            public GameState constructState(GameInitiation initiation, RoundGameContext context) {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }

}
