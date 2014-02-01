package com.clemble.casino.server.game.action;

import com.clemble.casino.game.MatchGameRecord;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.game.event.server.GameManagementEvent;

public interface GameProcessor<State extends GameState> {

    public GameManagementEvent process(final MatchGameRecord<State> session, final GameAction move);

}
