package com.gogomaya.client.game.service;

import static com.gogomaya.utils.Preconditions.checkNotNull;

import com.gogomaya.event.ClientEvent;
import com.gogomaya.event.listener.ConstructionEventSelector;
import com.gogomaya.event.listener.EventListener;
import com.gogomaya.event.listener.EventListenersManager;
import com.gogomaya.game.Game;
import com.gogomaya.game.GameSessionKey;
import com.gogomaya.game.construct.GameConstruction;
import com.gogomaya.game.construct.GameRequest;
import com.gogomaya.game.event.schedule.InvitationAcceptedEvent;
import com.gogomaya.game.event.schedule.InvitationDeclinedEvent;
import com.gogomaya.game.event.schedule.InvitationResponseEvent;
import com.gogomaya.game.service.GameConstructionService;

public class SimpleGameConstructionOperations implements GameConstructionOperations {

    final private String player;
    final private Game game;
    final private EventListenersManager listenersManager;
    final private GameConstructionService constructionService;

    public SimpleGameConstructionOperations(String player, Game game, GameConstructionService constructionService, EventListenersManager listenersManager) {
        this.player = player;
        this.game = game;
        this.constructionService = checkNotNull(constructionService);
        this.listenersManager = checkNotNull(listenersManager);
    }

    @Override
    public GameConstruction construct(GameRequest gameRequest) {
        return constructionService.construct(player, gameRequest);
    }

    @Override
    public GameConstruction getConstruct(long session) {
        return constructionService.getConstruct(player, session);
    }

    @Override
    public GameConstruction accept(long sessionId) {
        return response(sessionId, new InvitationAcceptedEvent(new GameSessionKey(game, sessionId), player));
    }

    @Override
    public GameConstruction decline(long sessionId) {
        return response(sessionId, new InvitationDeclinedEvent(player, new GameSessionKey(game, sessionId)));
    }

    @Override
    public GameConstruction response(long sessionId, InvitationResponseEvent responce) {
        return constructionService.reply(player, sessionId, responce);
    }

    @Override
    public ClientEvent getResponce(long session, String player) {
        return constructionService.getResponce(player, session, player);
    }

    @Override
    public void subscribe(long session, EventListener constructionListener) {
        listenersManager.subscribe(new ConstructionEventSelector(new GameSessionKey(game, session)), constructionListener);
    }

    @Override
    public String getGameActionServer(long sessionId) {
        return constructionService.getServer(player, sessionId);
    }

}
