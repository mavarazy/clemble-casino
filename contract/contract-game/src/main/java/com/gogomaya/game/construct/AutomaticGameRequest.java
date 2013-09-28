package com.gogomaya.game.construct;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gogomaya.game.specification.GameSpecification;
import com.gogomaya.player.PlayerAware;

@JsonTypeName("automatic")
public class AutomaticGameRequest extends GameRequest {

    /**
     * Generated 25/06/13
     */
    private static final long serialVersionUID = -529992778342722143L;

    public AutomaticGameRequest(String player, GameSpecification specification) {
        super(player, specification);
    }

    @JsonCreator
    public AutomaticGameRequest(@JsonProperty(PlayerAware.JSON_ID) String player, @JsonProperty("specification") GameSpecification specification,
            @JsonProperty(value = "pariticipants", required = false) Collection<Long> participants) {
        super(player, specification);
    }

}
