package com.clemble.casino.event;

import com.clemble.casino.game.action.GameAction;
import com.clemble.casino.server.hibernate.AbstractJsonHibernateType;

public class ClientEventHibernate extends AbstractJsonHibernateType<GameEvent>{

    public ClientEventHibernate() {
        super(GameEvent.class);
    }

}
