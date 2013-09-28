package com.gogomaya.server.player.notification;

import com.gogomaya.event.Event;

public interface PlayerNotificationListener<T extends Event> {

    public void onUpdate(String player, T event);

}
