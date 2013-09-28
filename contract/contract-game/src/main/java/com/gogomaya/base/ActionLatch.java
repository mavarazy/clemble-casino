package com.gogomaya.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gogomaya.error.GogomayaError;
import com.gogomaya.error.GogomayaException;
import com.gogomaya.event.ClientEvent;

public class ActionLatch implements Serializable {

    /**
     * Generated 04/07/13
     */
    private static final long serialVersionUID = -7689529505293361503L;

    final private Map<String, ClientEvent> actions = new HashMap<String, ClientEvent>();
    final private Class<?> expectedClass;

    public ActionLatch(final String participant, final String action) {
        this(Collections.singleton(participant), action);
    }

    public ActionLatch(final Collection<String> participants, final String action) {
        this(participants, action, null);
    }

    public ActionLatch(final Collection<String> participants, final String action, final Class<?> expectedClass) {
        this.expectedClass = expectedClass;
        for (String participant : participants) {
            actions.put(participant, new ExpectedAction(participant, action));
        }
    }

    public ActionLatch(final String[] participants, final String action, final Class<?> expectedClass) {
        this.expectedClass = expectedClass;
        for (String participant : participants) {
            actions.put(participant, new ExpectedAction(participant, action));
        }
    }

    public ActionLatch(final String player, String action, Class<?> expectedClass) {
        this.actions.put(player, new ExpectedAction(player, action));
        this.expectedClass = expectedClass;
    }

    @JsonCreator
    public ActionLatch(@JsonProperty("actions") final Collection<ClientEvent> expectedActions) {
        this.expectedClass = null;
        for (ClientEvent expectedAction : expectedActions) {
            actions.put(expectedAction.getPlayer(), expectedAction);
        }
    }

    public boolean contains(String participant) {
        return actions.keySet().contains(participant);
    }

    public boolean acted(String player) {
        return !(actions.get(player) instanceof ExpectedAction);
    }

    public Set<String> fetchParticipants() {
        return actions.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T extends ClientEvent> Collection<T> getActions() {
        return (Collection<T>) actions.values();
    }

    public Map<String, ClientEvent> fetchActionsMap() {
        return actions;
    }

    public ClientEvent fetchAction(String player) {
        return actions.get(player);
    }

    public ClientEvent put(String participant, ClientEvent action) {
        ClientEvent event = actions.get(participant);
        if (event instanceof ExpectedAction) {
            if (expectedClass != null && action.getClass() != expectedClass)
                throw GogomayaException.fromError(GogomayaError.GamePlayWrongMoveType);
            return actions.put(participant, action);
        } else if (event != null) {
            throw GogomayaException.fromError(GogomayaError.GamePlayMoveAlreadyMade);
        }
        throw GogomayaException.fromError(GogomayaError.GamePlayNoMoveExpected);
    }

    public boolean complete() {
        for (ClientEvent action : actions.values())
            if (action instanceof ExpectedAction)
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actions == null) ? 0 : actions.hashCode());
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
        ActionLatch other = (ActionLatch) obj;
        if (actions == null) {
            if (other.actions != null)
                return false;
        } else if (!actions.equals(other.actions))
            return false;
        return true;
    }

}
