package com.clemble.casino.server.game.aspect.match;

import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.game.GamePlayerAccount;
import com.clemble.casino.game.GamePlayerContext;
import com.clemble.casino.game.MatchGameContext;
import com.clemble.casino.game.event.RoundEndedEvent;
import com.clemble.casino.server.game.aspect.GameAspect;

public class MatchFillReminderAspect extends GameAspect<RoundEndedEvent> {

    final private MatchGameContext context;

    public MatchFillReminderAspect(MatchGameContext context) {
        super(new EventTypeSelector(RoundEndedEvent.class));
        this.context = context;
    }

    @Override
    public void doEvent(RoundEndedEvent event) {
        // Step 1. Filling pot with the reminder
        for (GamePlayerContext playerContext : event.getState().getContext().getPlayerContexts()) {
            GamePlayerAccount playerMatchAccount = playerContext.getAccount();
            GamePlayerAccount playerPotAccount = context.getPlayerContext(playerContext.getPlayer()).getAccount();
            playerPotAccount.subLeft(playerMatchAccount.getSpent() + playerPotAccount.getLeft());
            playerPotAccount.addOwned(playerMatchAccount.getOwned());
            context.add(playerPotAccount.getLeft());
        }
    }



}
