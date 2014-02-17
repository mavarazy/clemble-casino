package com.clemble.casino.integration.emulator;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.specification.MatchGameConfiguration;
import com.clemble.casino.integration.game.MatchGamePlayer;
import com.clemble.casino.integration.game.MatchGamePlayerFactory;
import com.clemble.casino.integration.game.construction.PlayerScenarios;

public class PlayerEmulator<State extends GameState> implements Runnable {

    final private Logger logger = LoggerFactory.getLogger(PlayerEmulator.class);

    final private MatchGameConfiguration specification;
    final private PlayerScenarios playerOperations;
    final private MatchGamePlayerFactory<State> sessionPlayerFactory;
    final private GameActor<State> actor;
    final private AtomicBoolean continueEmulation = new AtomicBoolean(true);
    final private AtomicLong lastMoved = new AtomicLong();
    final private AtomicReference<MatchGamePlayer<State>> currentPlayer = new AtomicReference<MatchGamePlayer<State>>();

    public PlayerEmulator(final GameActor<State> actor,
            final PlayerScenarios playerOperations,
            final MatchGamePlayerFactory<State> sessionPlayerFactory,
            final MatchGameConfiguration specification) {
        this.specification = checkNotNull(specification);
        this.actor = checkNotNull(actor);
        this.sessionPlayerFactory = checkNotNull(sessionPlayerFactory);
        this.playerOperations = checkNotNull(playerOperations);
    }

    public MatchGameConfiguration getSpecification() {
        return specification;
    }

    @Override
    public void run() {
        while (continueEmulation.get()) {
            ClembleCasinoOperations player = null;
            try {
                player = playerOperations.createPlayer();
                // Step 1. Start player emulator
                GameConstruction playerConstruction = player.gameConstructionOperations().constructAutomatch(specification);
                MatchGamePlayer<State> playerState = sessionPlayerFactory.construct(player, playerConstruction);
                logger.info("Registered {} with construction {} ", playerState.playerOperations(), playerState.getSession());
                currentPlayer.set(playerState);
                lastMoved.set(System.currentTimeMillis());
                playerState.waitForStart(0);
                while (playerState.isAlive()) {
                    // Step 2. Waiting for player turn
                    playerState.waitForTurn();
                    // Step 3. Performing action
                    if (playerState.isToMove()) {
                        int versionBefore = playerState.getVersion();
                        actor.move(playerState);
                        playerState.waitVersion(versionBefore + 1);
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                player.close();
            }
        }
    }

    public boolean isAlive() {
        return currentPlayer.get().isAlive() || (lastMoved.get() + TimeUnit.MINUTES.toMillis(15) < System.currentTimeMillis());
    }

    public void stop() {
        this.continueEmulation.set(false);
        this.currentPlayer.get().close();
    }

    public long getLastMoved() {
        return lastMoved.get();
    }
}
