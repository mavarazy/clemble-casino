package com.gogomaya.server.integration.game.construction;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.gogomaya.game.Game;
import com.gogomaya.game.GameSessionKey;
import com.gogomaya.game.GameState;
import com.gogomaya.game.configuration.GameSpecificationOptions;
import com.gogomaya.game.configuration.SelectSpecificationOptions;
import com.gogomaya.game.construct.AutomaticGameRequest;
import com.gogomaya.game.construct.AvailabilityGameRequest;
import com.gogomaya.game.construct.GameConstruction;
import com.gogomaya.game.construct.GameRequest;
import com.gogomaya.game.event.schedule.InvitationAcceptedEvent;
import com.gogomaya.game.event.schedule.InvitationDeclinedEvent;
import com.gogomaya.game.event.schedule.InvitationResponseEvent;
import com.gogomaya.game.specification.GameSpecification;
import com.gogomaya.server.integration.game.GameSessionPlayer;
import com.gogomaya.server.integration.game.GameSessionPlayerFactory;
import com.gogomaya.server.integration.player.Player;

abstract public class AbstractGameConstructionOperation<State extends GameState> implements GameConstructionOperations<State> {

    /**
     * Generated 03/07/13
     */
    private static final long serialVersionUID = 3499425460989980680L;

    final private Game game;
    final private GameSessionPlayerFactory<State> playerFactory;

    protected AbstractGameConstructionOperation(final Game name, final GameSessionPlayerFactory<State> playerFactory) {
        this.game = checkNotNull(name);
        this.playerFactory = checkNotNull(playerFactory);
    }

    @Override
    final public Game getGame() {
        return game;
    }

    @Override
    final public GameSpecification selectSpecification(Player player) {
        GameSpecificationOptions specificationOptions = getOptions(getGame(), player);
        if (specificationOptions instanceof SelectSpecificationOptions) {
            return ((SelectSpecificationOptions) specificationOptions).getSpecifications().get(0);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    final public GameSpecificationOptions getOptions() {
        return getOptions(game, null);
    }

    @Override
    final public GameSpecificationOptions getOptions(Player player) {
        return getOptions(game, player);
    }

    abstract public GameSpecificationOptions getOptions(Game game, Player player);

    @Override
    final public GameSessionPlayer<State> constructAutomatic(Player player) {
        return constructAutomatic(player, selectSpecification(player));
    }

    @Override
    public GameSessionPlayer<State> constructAutomatic(Player player, GameSpecification specification) {
        GameRequest automaticGameRequest = new AutomaticGameRequest(player.getPlayer(), specification);
        return construct(player, automaticGameRequest);
    }

    public GameSessionPlayer<State> constructAvailability(Player player, GameSpecification specification, Collection<String> participants) {
        GameRequest availabilityGameRequest = new AvailabilityGameRequest(player.getPlayer(), specification, participants);

        return construct(player, availabilityGameRequest);
    }

    @Override
    public GameSessionPlayer<State> construct(Player player, GameRequest request) {
        GameConstruction construction = request(player, request);
        return playerFactory.construct(player, construction);
    }

    abstract protected GameConstruction request(Player player, GameRequest request);

    @Override
    final public GameSessionPlayer<State> acceptInvitation(Player player, GameSessionKey construction) {
        // Step 1. Need to start listening before sending accept, otherwise constructed event might be missed
        GameSessionPlayer<State> sessionPlayer = playerFactory.construct(player, construction);
        // Step 2. Sending accept message to the server
        InvitationResponseEvent acceptedEvents = new InvitationAcceptedEvent(construction, player.getPlayer());
        response(player, acceptedEvents);
        return sessionPlayer;
    }

    @Override
    final public void declineInvitation(Player player, GameSessionKey construction) {
        InvitationResponseEvent declinedEvents = new InvitationDeclinedEvent(player.getPlayer(), construction);
        response(player, declinedEvents);
    }

    abstract protected void response(Player player, InvitationResponseEvent responseEvent);

}
