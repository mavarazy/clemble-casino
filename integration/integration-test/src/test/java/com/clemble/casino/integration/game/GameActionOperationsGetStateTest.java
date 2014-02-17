package com.clemble.casino.integration.game;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.client.game.GameActionOperations;
import com.clemble.casino.client.game.GameConstructionOperations;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.event.server.GameInitiationCanceledEvent;
import com.clemble.casino.game.specification.MatchGameConfiguration;
import com.clemble.casino.integration.event.EventAccumulator;
import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.server.game.construct.ServerGameInitiationService;
import com.clemble.test.concurrent.AsyncCompletionUtils;
import com.clemble.test.concurrent.Check;
import com.clemble.test.concurrent.Get;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class GameActionOperationsGetStateTest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Autowired
    public GameScenarios gameScenarios;

    @Test
    public void automaticConstruction() {
        // Step 1. Creating random player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();
        // Step 2. Constructing automatch game
        GameConstructionOperations<NumberState> constructionOperations = A.gameConstructionOperations(Game.num);
        List<MatchGameConfiguration> configurations = constructionOperations.getConfigurations().matchConfigurations();
        MatchGameConfiguration configuration = configurations.get(0);
        GameConstruction construction = constructionOperations.constructAutomatch(configuration);
        // Step 3. Checking getState works fine
        final GameActionOperations<NumberState> aoA = A.gameActionOperations(construction.getSession());
        assertNull(aoA.getState());
        // Step 4. Creating construction from B side
        B.gameConstructionOperations(Game.num).constructAutomatch(configuration);
        // Step 5. Checking value is not null anymore
        gameScenarios.match(construction.getSession(), A).waitForStart();
        AsyncCompletionUtils.<GameState>get(new Get<GameState>() {
            @Override
            public GameState get() {
                return aoA.getState();
            }
        }, 30_000);
    }

    @Test
    public void automaticConstructionWithDisabled() {
        // Step 1. Creating random player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        ClembleCasinoOperations B = playerScenarios.createPlayer();
        // Step 1.1. Accumulating Game initiation events
        final EventAccumulator<GameInitiationCanceledEvent> listener = new EventAccumulator<>();
        B.listenerOperations().subscribe(new EventTypeSelector(GameInitiationCanceledEvent.class), listener);
        // Step 2. Constructing automatch game
        GameConstructionOperations<NumberState> constructionOperations = A.gameConstructionOperations(Game.num);
        List<MatchGameConfiguration> specificationOptions = constructionOperations.getConfigurations().matchConfigurations();
        MatchGameConfiguration specification = specificationOptions.get(0);
        constructionOperations.constructAutomatch(specification);
        // Step 3. Checking getState works fine
        A.close();
        // Step 4. Creating construction from B side
        B.gameConstructionOperations(Game.num).constructAutomatch(specification);
        // Step 5. Checking value is not null anymore
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(ServerGameInitiationService.CANCEL_TIMEOUT_SECONDS));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        AsyncCompletionUtils.check(new Check() {
            @Override
            public boolean check() {
                return listener.toList().size() == 1;
            }
        }, 10_000);
    }

}
