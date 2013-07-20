package com.gogomaya.server.spring.game;

import java.util.Collection;

import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

import com.gogomaya.server.ServerRegistry;
import com.gogomaya.server.game.GameState;
import com.gogomaya.server.game.action.impl.GamePostProcessorListener;
import com.gogomaya.server.game.action.impl.VerificationGameProcessorListener;
import com.gogomaya.server.game.configuration.GameSpecificationRegistry;
import com.gogomaya.server.game.construct.GameConstruction;
import com.gogomaya.server.game.construct.GameConstructionService;
import com.gogomaya.server.game.construct.GameInitiation;
import com.gogomaya.server.game.construct.GameInitiatorService;
import com.gogomaya.server.game.construct.SimpleGameConstructionService;
import com.gogomaya.server.game.notification.TableServerRegistry;
import com.gogomaya.server.money.Money;
import com.gogomaya.server.payment.PaymentOperation;
import com.gogomaya.server.payment.PaymentTransaction;
import com.gogomaya.server.payment.PaymentTransactionService;
import com.gogomaya.server.player.PlayerProfile;
import com.gogomaya.server.player.account.PlayerAccountService;
import com.gogomaya.server.player.lock.PlayerLockService;
import com.gogomaya.server.player.notification.PlayerNotificationService;
import com.gogomaya.server.player.state.PlayerStateManager;
import com.gogomaya.server.repository.game.GameConstructionRepository;
import com.gogomaya.server.spring.common.CommonSpringConfiguration;
import com.gogomaya.server.spring.common.SpringConfiguration;
import com.gogomaya.server.spring.player.PlayerCommonSpringConfiguration;

@Configuration
@Import(value = { CommonSpringConfiguration.class, PlayerCommonSpringConfiguration.class, GameManagementSpringConfiguration.DefaultAndTest.class,
        GameManagementSpringConfiguration.Cloud.class, GameManagementSpringConfiguration.Test.class })
public class GameManagementSpringConfiguration implements SpringConfiguration {

    @Autowired
    @Qualifier("paymentTransactionService")
    public PaymentTransactionService paymentTransactionService;

    @Autowired
    @Qualifier("playerStateManager")
    public PlayerStateManager playerStateManager;

    @Bean
    @Singleton
    public <State extends GameState> VerificationGameProcessorListener<State> verificationGameProcessorListener() {
        return new VerificationGameProcessorListener<State>();
    }

    @Bean
    @Singleton
    public <State extends GameState> GamePostProcessorListener<State> gamePostProcessorListener() {
        return new GamePostProcessorListener<State>(playerStateManager, paymentTransactionService);
    }

    @Bean
    @Singleton
    public GameSpecificationRegistry gameSpecificationRegistry() {
        return new GameSpecificationRegistry();
    }

    @Profile(value = { "default", "test" })
    public static class DefaultAndTest {

        @Bean
        @Singleton
        public TableServerRegistry tableServerRegistry() {
            ServerRegistry serverRegistry = new ServerRegistry();
            serverRegistry.register(10000L, "localhost");
            return new TableServerRegistry(serverRegistry);
        }

    }

    @Configuration
    @Profile(value = { "test" })
    public static class Test {

        @Autowired
        public PlayerLockService playerLockService;

        @Autowired
        public PlayerStateManager playerStateManager;

        @Autowired
        public PlayerNotificationService playerNotificationService;

        @Autowired
        public GameConstructionRepository constructionRepository;

        @Bean
        @Singleton
        public GameConstructionService gameConstructionService() {
            return new SimpleGameConstructionService(playerWalletService(), playerNotificationService, constructionRepository, initiatorService(),
                    playerLockService, playerStateManager);
        }

        @Bean
        @Singleton
        public GameInitiatorService initiatorService() {
            return new GameInitiatorService() {
                @Override
                public void initiate(GameConstruction construction) {
                }

                @Override
                public boolean initiate(GameInitiation initiation) {
                    return true;
                }
            };
        }

        @Bean
        @Singleton
        public PaymentTransactionService paymentTransactionService() {
            return new PaymentTransactionService() {

                @Override
                public PaymentTransaction process(PaymentTransaction paymentTransaction) {
                    return paymentTransaction;
                }

                @Override
                public PaymentTransaction register(PlayerProfile playerId) {
                    return new PaymentTransaction();
                }
            };
        }

        @Bean
        @Singleton
        public PlayerAccountService playerWalletService() {
            return new PlayerAccountService() {
                @Override
                public boolean canAfford(PaymentOperation paymentOperation) {
                    return true;
                }

                @Override
                public boolean canAfford(long playerId, Money ammount) {
                    return true;
                }

                @Override
                public boolean canAfford(Collection<Long> playerId, Money ammount) {
                    return true;
                }
            };
        }

    }

    @Profile(value = { "cloud" })
    public static class Cloud {

        @Autowired
        @Qualifier("tableQueueTemplate")
        public RedisTemplate<byte[], Long> redisTemplate;

        @Bean
        @Singleton
        public TableServerRegistry tableServerRegistry() {
            ServerRegistry serverRegistry = new ServerRegistry();
            serverRegistry.register(10_000L, "gogomaya.cloudfoundry.com");
            return new TableServerRegistry(serverRegistry);
        }

    }

}
