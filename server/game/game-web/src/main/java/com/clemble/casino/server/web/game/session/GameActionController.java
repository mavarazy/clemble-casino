package com.clemble.casino.server.web.game.session;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.server.ExternalController;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.game.action.MadeMove;
import com.clemble.casino.game.event.server.GameManagementEvent;
import com.clemble.casino.game.service.GameActionService;
import com.clemble.casino.server.game.action.MatchGameManager;
import com.clemble.casino.server.game.cache.GameCacheService;
import com.clemble.casino.server.repository.game.GameSessionRepository;
import com.clemble.casino.web.game.GameWebMapping;
import com.clemble.casino.web.mapping.WebMapping;

@Controller
public class GameActionController<State extends GameState> implements GameActionService<State>, ExternalController {

    final private GameCacheService<State> cacheService;
    final private MatchGameManager<State> matchManager;
    final private GameSessionRepository<State> sessionRepository;

    public GameActionController(final GameSessionRepository<State> sessionRepository, final GameCacheService<State> cacheService,
            final MatchGameManager<State> sessionProcessor) {
        this.cacheService = checkNotNull(cacheService);
        this.matchManager = checkNotNull(sessionProcessor);
        this.sessionRepository = checkNotNull(sessionRepository);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = GameWebMapping.GAME_SESSIONS_ACTIONS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    GameManagementEvent process(@PathVariable("game") Game game, @PathVariable("session") String session, @RequestBody GameAction move) {
        // Step 1. Retrieving associated table
        return matchManager.process(new GameSessionKey(game, session), move);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GameWebMapping.GAME_SESSIONS_STATE, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    State getState(@PathVariable("game") Game game, @PathVariable("session") String session) {
        GameSessionKey sessionKey = new GameSessionKey(game, session);
        if (!sessionRepository.exists(sessionKey))
            return null;
        return cacheService.get(sessionKey).getSession().getState();
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GameWebMapping.GAME_SESSIONS_ACTIONS_ACTION, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody MadeMove getAction(@PathVariable("game") Game game, @PathVariable("sessionId") String session, @PathVariable("actionId") int actionId) {
        return sessionRepository.findAction(new GameSessionKey(game, session), actionId);
    }
}
