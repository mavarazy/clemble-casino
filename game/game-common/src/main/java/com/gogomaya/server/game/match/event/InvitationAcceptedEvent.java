package com.gogomaya.server.game.match.event;

import com.gogomaya.server.player.PlayerAware;

public class InvitationAcceptedEvent extends ScheduledGameEvent implements PlayerAware {

    /**
     * Generated 02/06/2013
     */
    private static final long serialVersionUID = -4465974655141746411L;

    private long scheduledGameId;

    private long playerId;

    @Override
    public long getScheduledGameId() {
        return scheduledGameId;
    }

    public void setScheduledGameId(long scheduledGameId) {
        this.scheduledGameId = scheduledGameId;
    }

    @Override
    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

}
