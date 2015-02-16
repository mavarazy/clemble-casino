package com.clemble.casino.server.social.controller;

import static com.clemble.casino.social.SocialWebMapping.*;
import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.registration.service.PlayerSocialRegistrationService;
import com.clemble.casino.server.social.SocialConnectionDataAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.clemble.casino.error.ClembleCasinoValidationService;
import com.clemble.casino.registration.PlayerToken;
import com.clemble.casino.registration.PlayerSocialGrantRegistrationRequest;
import com.clemble.casino.registration.PlayerSocialRegistrationRequest;
import com.clemble.casino.server.ExternalController;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.security.PlayerTokenFactory;
import com.clemble.casino.WebMapping;

@RestController
public class PlayerSocialRegistrationController implements PlayerSocialRegistrationService, ExternalController {

    final private SocialConnectionDataAdapter registrationService;
    final private ClembleCasinoValidationService validationService;

    public PlayerSocialRegistrationController(
        final SocialConnectionDataAdapter registrationService,
        final ClembleCasinoValidationService validationService) {
        this.registrationService = registrationService;
        this.validationService = checkNotNull(validationService);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = SOCIAL_REGISTRATION_DESCRIPTION, produces = PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public String register(@RequestBody PlayerSocialRegistrationRequest socialRegistrationRequest) {
        validationService.validate(socialRegistrationRequest.getSocialConnectionData());
        // Step 1. Checking if this user already exists
        String player = registrationService.register(socialRegistrationRequest.getSocialConnectionData());
        // Step 2. All done continue
        return player;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = SOCIAL_REGISTRATION_GRANT, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public String register(@RequestBody PlayerSocialGrantRegistrationRequest grantRegistrationRequest) {
        validationService.validate(grantRegistrationRequest.getAccessGrant());
        // Step 1. Checking if this user already exists
        String player = registrationService.register(grantRegistrationRequest.getAccessGrant());
        // Step 2. All done continue
        return player;
    }

}
