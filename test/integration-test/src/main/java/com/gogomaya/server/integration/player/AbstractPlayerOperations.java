package com.gogomaya.server.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import com.gogomaya.server.integration.game.construction.GameConstructionOperations;
import com.gogomaya.server.integration.player.listener.PlayerListenerOperations;
import com.gogomaya.server.integration.player.profile.ProfileOperations;
import com.gogomaya.server.integration.player.session.SessionOperations;
import com.gogomaya.server.player.PlayerProfile;
import com.gogomaya.server.player.security.PlayerCredential;
import com.gogomaya.server.player.security.PlayerIdentity;
import com.gogomaya.server.player.web.RegistrationRequest;

abstract public class AbstractPlayerOperations implements PlayerOperations {

    final private PlayerListenerOperations listenerOperations;
    final private GameConstructionOperations<?>[] gameConstructionOprations;
    final private ProfileOperations playerProfileOperations;
    final private SessionOperations playerSessionOperations;

    protected AbstractPlayerOperations(PlayerListenerOperations listenerOperations,
            ProfileOperations profileOperations,
            SessionOperations sessionOperations,
            GameConstructionOperations<?>[] gameConstructionOprations) {
        this.listenerOperations = checkNotNull(listenerOperations);
        this.gameConstructionOprations = checkNotNull(gameConstructionOprations);
        this.playerSessionOperations = checkNotNull(sessionOperations);
        this.playerProfileOperations = checkNotNull(profileOperations);
    }

    @Override
    final public Player createPlayer() {
        return createPlayer(new PlayerProfile().setFirstName(RandomStringUtils.randomAlphabetic(10)).setLastName(RandomStringUtils.randomAlphabetic(10))
                .setNickName(RandomStringUtils.randomAlphabetic(10)));
    }

    @Override
    final public Player createPlayer(PlayerProfile playerProfile) {
        // Step 0. Sanity check
        checkNotNull(playerProfile);
        // Step 1. Creating RegistrationRequest for processing
        PlayerCredential playerCredential = new PlayerCredential().setEmail(RandomStringUtils.randomAlphabetic(30) + "@gmail.com").setPassword(
                UUID.randomUUID().toString());
        RegistrationRequest registrationRequest = new RegistrationRequest().setPlayerProfile(playerProfile).setPlayerCredential(playerCredential);
        // Step 2. Forwarding to appropriate method for processing
        return createPlayer(registrationRequest);
    }

    final public Player create(PlayerIdentity playerIdentity, PlayerCredential credential) {
        return new Player(playerIdentity, credential, this, playerProfileOperations, playerSessionOperations, listenerOperations, gameConstructionOprations);
    }

}
