package com.gogomaya.server.repository.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gogomaya.game.GameSessionKey;
import com.gogomaya.game.construct.ScheduledGame;

@Repository
public interface GameScheduleRepository extends JpaRepository<ScheduledGame, GameSessionKey> {

    public List<ScheduledGame> findByStartDateBetween(long startTime, long endTime);

}
