package com.clemble.casino.server.player.registration;

import com.clemble.casino.error.ClembleCasinoValidationService;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.player.SocialAccessGrant;
import com.clemble.casino.player.SocialConnectionData;
import com.clemble.casino.server.ServerService;
import com.clemble.casino.server.event.SystemPlayerSocialAddedEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.social.SocialConnection;
import com.clemble.casino.server.social.SocialConnectionAdapter;
import com.clemble.casino.server.social.SocialConnectionAdapterRegistry;
import com.clemble.casino.server.social.SocialConnectionDataAdapter;

import static com.google.common.base.Preconditions.checkNotNull;

public class ServerProfileSocialRegistrationService implements ServerService {

    final private SystemNotificationService notificationService;
    final private SocialConnectionAdapterRegistry socialAdapterRegistry;
    final private SocialConnectionDataAdapter socialConnectionDataAdapter;
    final private ClembleCasinoValidationService validationService;

    public ServerProfileSocialRegistrationService(
        final ClembleCasinoValidationService validationService,
        final SocialConnectionAdapterRegistry socialAdapterRegistry,
        final SystemNotificationService notificationService,
        final SocialConnectionDataAdapter socialConnectionDataAdapter) {
        this.validationService = checkNotNull(validationService);
        this.socialConnectionDataAdapter = checkNotNull(socialConnectionDataAdapter);
        this.socialAdapterRegistry = checkNotNull(socialAdapterRegistry);
        this.notificationService = checkNotNull(notificationService);
    }

    public PlayerProfile create(SocialConnectionData socialConnectionData) {
        validationService.validate(socialConnectionData);
        // Step 1. Registering player with SocialConnection
        SocialConnection socialConnection = socialConnectionDataAdapter.register(socialConnectionData);
        // Step 2. Fetch Player identity information
        return fetchPlayerProfile(socialConnection);
    }

    public PlayerProfile create(SocialAccessGrant accessGrant) {
        validationService.validate(accessGrant);
        // Step 1. Registering player with SocialConnection
        SocialConnection socialConnection = socialConnectionDataAdapter.register(accessGrant);
        // Step 2. Fetch Player identity information
        return fetchPlayerProfile(socialConnection);
    }

    private PlayerProfile fetchPlayerProfile(SocialConnection socialConnection) {
        // Step 1. Fetching player profile
        SocialConnectionAdapter adapter = socialAdapterRegistry.getSocialAdapter(socialConnection.getConnection().getKey().getProviderId());
        PlayerProfile playerProfile = adapter.fetchPlayerProfile(socialConnection.getConnection());
        playerProfile.setPlayer(socialConnection.getPlayer());
        // Step 2. Notifying of added social connection
        notificationService.notify(new SystemPlayerSocialAddedEvent(socialConnection.getPlayer(), socialConnection.getConnection().getKey()));
        // Step 3. Returning social connection
        return playerProfile;
    }

}
