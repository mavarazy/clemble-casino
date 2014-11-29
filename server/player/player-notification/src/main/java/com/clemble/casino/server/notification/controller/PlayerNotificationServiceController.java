package com.clemble.casino.server.notification.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.goal.GoalWebMapping;
import com.clemble.casino.notification.PlayerNotification;
import com.clemble.casino.player.PlayerNotificationWebMapping;
import com.clemble.casino.player.service.PlayerNotificationService;
import com.clemble.casino.server.notification.ServerPlayerNotification;
import com.clemble.casino.server.notification.repository.PlayerNotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavarazy on 11/29/14.
 */
@RestController
public class PlayerNotificationServiceController implements PlayerNotificationService {

    final private PlayerNotificationRepository notificationRepository;

    public PlayerNotificationServiceController(PlayerNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<PlayerNotification> myNotifications() {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET, value = PlayerNotificationWebMapping.MY_NOTIFICATIONS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<PlayerNotification> myNotifications(@CookieValue("player") String player) {
        List<ServerPlayerNotification> serverPlayerNotifications = notificationRepository.findByPlayer(player);
        return serverPlayerNotifications.stream().map((sn) -> sn.getNotification()).collect(Collectors.toList());
    }

}
