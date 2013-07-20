package com.gogomaya.server.game.event.server;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gogomaya.server.event.ClientEvent;
import com.gogomaya.server.game.GameState;
import com.gogomaya.server.game.GameTable;
import com.gogomaya.server.game.ServerResourse;
import com.gogomaya.server.game.SessionAware;

@JsonTypeName("started")
public class GameStartedEvent<State extends GameState> extends GameServerEvent<State> implements SessionAware {

    /**
     * Generated
     */
    private static final long serialVersionUID = -4474960027054354888L;

    private long session;

    private ServerResourse resource;

    private Collection<ClientEvent> nextMoves;

    public GameStartedEvent() {
    }

    public GameStartedEvent(long session, GameTable<State> table) {
        super(table.getCurrentSession());
        this.session = session;
        this.nextMoves = getState().getNextMoves();
        this.resource = table.fetchServerResourse();
    }

    public GameStartedEvent(SessionAware sessionAware) {
        super(sessionAware);
    }

    public GameStartedEvent(SessionAware sessionAware, State state) {
        super(sessionAware);
        this.setState(state);
    }

    public Collection<ClientEvent> getNextMoves() {
        return nextMoves;
    }

    public GameStartedEvent<State> setNextMoves(Collection<ClientEvent> nextMoves) {
        this.nextMoves = nextMoves;
        return this;
    }

    public ServerResourse getResource() {
        return resource;
    }

    public void setResource(ServerResourse resource) {
        this.resource = resource;
    }

    @Override
    public long getSession() {
        return session;
    }

    public GameServerEvent<State> setSession(long construction) {
        this.session = construction;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nextMoves == null) ? 0 : nextMoves.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameStartedEvent<State> other = (GameStartedEvent<State>) obj;
        if (nextMoves == null) {
            if (other.nextMoves != null)
                return false;
        } else if (!nextMoves.equals(other.nextMoves))
            return false;
        return true;
    }

}
