package com.clemble.casino.server.registration.controller;

import com.clemble.casino.security.ClembleConsumerDetails;
import com.clemble.casino.registration.PlayerCredential;
import com.clemble.casino.registration.PlayerToken;
import com.clemble.casino.registration.service.PlayerBaseRegistrationService;
import com.clemble.casino.registration.service.PlayerManualRegistrationService;
import com.clemble.casino.registration.PlayerBaseRegistrationRequest;
import com.clemble.casino.registration.PlayerLoginRequest;
import com.clemble.casino.registration.PlayerRegistrationRequest;
import com.clemble.casino.web.mapping.WebMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.clemble.casino.web.player.PlayerWebMapping.REGISTRATION_BASIC_LOGIN;
import static com.clemble.casino.web.player.PlayerWebMapping.REGISTRATION_BASIC_PROFILE;

/**
 * Created by mavarazy on 7/28/14.
 */
@Controller
public class PlayerBaseRegistrationController implements PlayerBaseRegistrationService {

    final private ClembleConsumerDetails DEFAULT_DETAILS = new ClembleConsumerDetails("DEFAULT", "web", null, null,null);

    final private PlayerManualRegistrationService manualRegistrationService;

    public PlayerBaseRegistrationController(PlayerManualRegistrationService manualRegistrationService) {
        this.manualRegistrationService = manualRegistrationService;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = REGISTRATION_BASIC_LOGIN, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody PlayerToken login(@RequestBody PlayerCredential playerCredentials) {
        // Step 1. Propagating request
        return manualRegistrationService.login(new PlayerLoginRequest(DEFAULT_DETAILS, playerCredentials));
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = REGISTRATION_BASIC_PROFILE, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody PlayerToken register(@RequestBody PlayerBaseRegistrationRequest registrationRequest) {
        return manualRegistrationService.createPlayer(new PlayerRegistrationRequest(DEFAULT_DETAILS, registrationRequest.getPlayerCredential(), registrationRequest.getPlayerProfile()));
    }

}
