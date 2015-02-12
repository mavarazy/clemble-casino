package com.clemble.casino.integration.goal;

import com.clemble.casino.goal.lifecycle.construction.GoalSuggestion;
import com.clemble.casino.goal.lifecycle.construction.GoalSuggestionRequest;
import com.clemble.casino.goal.lifecycle.construction.service.GoalSuggestionService;
import com.clemble.casino.goal.suggestion.controller.GoalSuggestionServiceController;

import java.util.List;

/**
 * Created by mavarazy on 1/3/15.
 */
public class IntegrationGoalSuggestionService implements GoalSuggestionService {

    final private String player;
    final private GoalSuggestionServiceController suggestionService;

    public IntegrationGoalSuggestionService(String player, GoalSuggestionServiceController suggestionServiceController) {
        this.player = player;
        this.suggestionService = suggestionServiceController;
    }

    @Override
    public List<GoalSuggestion> listMy() {
        return suggestionService.listMy(player);
    }

    @Override
    public List<GoalSuggestion> list(String player) {
        return suggestionService.list(player);
    }

    @Override
    public List<GoalSuggestion> listMySuggested() {
        return suggestionService.listMySuggested(player);
    }

    @Override
    public List<GoalSuggestion> listSuggested(String player) {
        return suggestionService.listSuggested(player);
    }

    @Override
    public GoalSuggestion getSuggestion(String goalKey) {
        return suggestionService.getSuggestion(goalKey);
    }

    @Override
    public GoalSuggestion addSuggestion(String player, GoalSuggestionRequest suggestionRequest) {
        return suggestionService.addSuggestion(this.player, player, suggestionRequest);
    }

    @Override
    public GoalSuggestion reply(String goalKey, boolean accept) {
        return suggestionService.reply(player, goalKey, accept);
    }

}
