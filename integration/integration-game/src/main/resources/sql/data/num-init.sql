    DELETE FROM GAME_SPECIFICATION WHERE GAME_NAME = 'num';

    INSERT INTO GAME_SPECIFICATION (GAME_NAME, SPECIFICATION_NAME, BET_RULE, PRICE, CURRENCY, GIVE_UP, MOVE_TIME_LIMIT, MOVE_TIME_PUNISHMENT, PLAYER_NUMBER, PRIVACY_RULE, TOTAL_TIME_LIMIT, TOTAL_TIME_PUNISHMENT, VISIBILITY, WON, DRAW, ROLES)
    VALUES ('num', 'low', '{"betType": "unlimited"}', 50, 0, 'all', 2000, 'loose', 'two', 'everybody', 4000, 'loose', 'visible', 'price', 'owned', '{"A", "B"}');
