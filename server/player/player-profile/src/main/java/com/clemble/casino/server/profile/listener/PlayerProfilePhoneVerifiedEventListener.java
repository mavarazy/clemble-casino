package com.clemble.casino.server.profile.listener;

import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.server.event.email.SystemEmailVerifiedEvent;
import com.clemble.casino.server.event.phone.SystemPhoneVerifiedEvent;
import com.clemble.casino.server.player.notification.SystemEventListener;
import com.clemble.casino.server.profile.repository.PlayerProfileRepository;
import org.springframework.social.connect.ConnectionKey;

/**
 * Created by mavarazy on 12/12/14.
 */
public class PlayerProfilePhoneVerifiedEventListener implements SystemEventListener<SystemPhoneVerifiedEvent> {

    final private PlayerProfileRepository profileRepository;

    public PlayerProfilePhoneVerifiedEventListener(PlayerProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void onEvent(SystemPhoneVerifiedEvent event) {
        // Step 1. Fetching player profile
        PlayerProfile profile = profileRepository.findOne(event.getPlayer());
        // Step 2. Adding new connection to SocialConnections
        profile.addSocialConnection(new ConnectionKey("phone", "verified"));
        // Step 3. Saving updated player profile
        profileRepository.save(profile);
    }

    @Override
    public String getChannel() {
        return SystemPhoneVerifiedEvent.CHANNEL;
    }

    @Override
    public String getQueueName() {
        return SystemPhoneVerifiedEvent.CHANNEL + " > player:profile";
    }
}
