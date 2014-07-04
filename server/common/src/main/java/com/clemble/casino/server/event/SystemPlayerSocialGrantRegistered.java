package com.clemble.casino.server.event;

import com.clemble.casino.player.SocialAccessGrant;
import com.clemble.casino.player.SocialAccessGrantAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mavarazy on 7/4/14.
 */
public class SystemPlayerSocialGrantRegistered
    extends SystemPlayerRegisteredEvent
    implements SocialAccessGrantAware {

    final private SocialAccessGrant socialGrant;

    @JsonCreator
    public SystemPlayerSocialGrantRegistered(@JsonProperty("player") String player, @JsonProperty("accessGrant") SocialAccessGrant socialGrant) {
        super(player);
        this.socialGrant = socialGrant;
    }

    @Override
    public SocialAccessGrant getAccessGrant() {
        return socialGrant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SystemPlayerSocialGrantRegistered that = (SystemPlayerSocialGrantRegistered) o;

        if (socialGrant != null ? !socialGrant.equals(that.socialGrant) : that.socialGrant != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (socialGrant != null ? socialGrant.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + ":social:grant";
    }

}
