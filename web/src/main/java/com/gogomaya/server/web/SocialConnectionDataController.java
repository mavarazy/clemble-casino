package com.gogomaya.server.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gogomaya.server.social.SocialConnectionDataAdapter;
import com.gogomaya.server.user.SocialConnectionData;

@Controller
public class SocialConnectionDataController {

    final private SocialConnectionDataAdapter socialConnectionDataAdapter;

    public SocialConnectionDataController(final SocialConnectionDataAdapter socialConnectionDataAdapter) {
        this.socialConnectionDataAdapter = socialConnectionDataAdapter;
    }

    @RequestMapping(method = RequestMethod.POST, value="/profile/social", produces = "application/json")
    @ResponseStatus(value= HttpStatus.OK)
    public String createUser(@RequestBody SocialConnectionData socialConnectionData) {
        return socialConnectionDataAdapter.adapt(socialConnectionData);
    }

}
