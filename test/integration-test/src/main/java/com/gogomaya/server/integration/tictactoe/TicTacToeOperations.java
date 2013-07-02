package com.gogomaya.server.integration.tictactoe;

import com.gogomaya.server.event.ClientEvent;
import com.gogomaya.server.game.tictactoe.TicTacToeState;
import com.gogomaya.server.integration.game.GamePlayerOperations;

public interface TicTacToeOperations extends GamePlayerOperations<TicTacToeState> {

    public TicTacToeState select(TicTacToePlayer player, int row, int column);

    public TicTacToeState bet(TicTacToePlayer player, int ammount);

    public TicTacToeState giveUp(TicTacToePlayer player);

    public TicTacToeState perform(TicTacToePlayer player, ClientEvent action);

}
