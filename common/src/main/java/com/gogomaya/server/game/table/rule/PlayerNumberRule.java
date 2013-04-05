package com.gogomaya.server.game.table.rule;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gogomaya.server.game.table.GameTableRule;

@Embeddable
public class PlayerNumberRule implements GameTableRule {

    /**
     * Generated 21/03/13
     */
    private static final long serialVersionUID = -2230312436839159909L;

    @Column(name = "TABLE_PLAYERS_MIN")
    private int minPlayers;

    @Column(name = "TABLE_PLAYERS_MAX")
    private int maxPlayers;

    public PlayerNumberRule() {
    }

    private PlayerNumberRule(int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        if (minPlayers > maxPlayers || minPlayers < 0) {
            throw new IllegalArgumentException();
        }
    }

    public int getMinPlayers() {
        return minPlayers;
    }
    
    public PlayerNumberRule setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public PlayerNumberRule setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public static PlayerNumberRule create(final int minPlayers, final int maxPlayers) {
        return new PlayerNumberRule(minPlayers, maxPlayers);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxPlayers;
        result = prime * result + minPlayers;
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
        PlayerNumberRule other = (PlayerNumberRule) obj;
        if (maxPlayers != other.maxPlayers)
            return false;
        if (minPlayers != other.minPlayers)
            return false;
        return true;
    }

}
