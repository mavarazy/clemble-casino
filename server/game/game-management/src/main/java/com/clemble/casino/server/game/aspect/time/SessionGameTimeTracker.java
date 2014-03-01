package com.clemble.casino.server.game.aspect.time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.clemble.casino.game.RoundGameContext;
import com.clemble.casino.game.RoundGamePlayerContext;
import com.clemble.casino.game.specification.RoundGameConfiguration;
import com.clemble.casino.player.PlayerAware;
import com.clemble.casino.server.game.action.ScheduledGameAction;
import com.clemble.casino.server.game.action.ScheduledGameActionExecutor;

public class SessionGameTimeTracker {

    final private Map<String, Collection<PlayerGameTimeTracker>> playerToTrackers;

    public SessionGameTimeTracker(final RoundGameConfiguration specification, RoundGameContext context, ScheduledGameAction action, ScheduledGameActionExecutor actionExecutor) {
        this.playerToTrackers = new HashMap<String, Collection<PlayerGameTimeTracker>>();
        for (RoundGamePlayerContext playerContext : context.getPlayerContexts()) {
            Collection<PlayerGameTimeTracker> trackers = new ArrayList<>();
            trackers.add(new PlayerGameTimeTracker(playerContext.getClock(), action, specification.getMoveTimeRule(), actionExecutor));
            trackers.add(new PlayerGameTimeTracker(playerContext.getClock(), action, specification.getTotalTimeRule(), actionExecutor));
            playerToTrackers.put(playerContext.getPlayer(), trackers);
        }
    }

    public void markMoved(PlayerAware move) {
        markMoved(move.getPlayer());
    }

    public void markMoved(String player) {
        for (PlayerGameTimeTracker playerTimeTracker : playerToTrackers.get(player)) {
            playerTimeTracker.markMoved();
        }
    }

    public void markToMove(PlayerAware nextMove) {
        markToMove(nextMove.getPlayer());
    }

    public void markToMove(String player) {
        for (PlayerGameTimeTracker playerTimeTracker : playerToTrackers.get(player)) {
            playerTimeTracker.markToMove();
        }
    }

}
