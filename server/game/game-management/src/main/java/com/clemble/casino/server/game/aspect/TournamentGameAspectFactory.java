package com.clemble.casino.server.game.aspect;

import com.clemble.casino.event.Event;
import com.clemble.casino.game.lifecycle.management.TournamentGameContext;
import com.clemble.casino.game.lifecycle.configuration.TournamentGameConfiguration;
import com.clemble.casino.server.game.action.TournamentGameState;

public interface TournamentGameAspectFactory<T extends Event> extends GameAspectFactory<T, TournamentGameState, TournamentGameConfiguration>{

}
