package com.clemble.casino.integration.game.construction;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.specification.GameConfiguration;
import com.clemble.casino.integration.game.GamePlayer;
import com.clemble.casino.integration.game.RoundGamePlayer;

public interface GameScenarios extends BaseGameScenarios {

    public GamePlayer construct(GameConfiguration configuration, ClembleCasinoOperations player);

    public <T extends RoundGamePlayer<?>> T match(Game game, ClembleCasinoOperations player);

    public <T extends RoundGamePlayer<?>> T match(GameConfiguration configuration, ClembleCasinoOperations player);

    public <T extends RoundGamePlayer<?>> T match(Game game, ClembleCasinoOperations player, String... players);

    public <T extends RoundGamePlayer<?>> T match(GameConfiguration configuration, ClembleCasinoOperations player, String... players);
    
    public <T extends RoundGamePlayer<?>> T match(GameSessionKey sessionKey, ClembleCasinoOperations player);

    public <T extends RoundGamePlayer<?>> T accept(GameSessionKey sessionKey, ClembleCasinoOperations player);

}
