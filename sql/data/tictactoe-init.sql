
    INSERT INTO GAME_SPECIFICATION (GAME_NAME, SPECIFICATION_NAME, PRICE, CURRENCY, GIVE_UP, MOVE_TIME_LIMIT, MOVE_TIME_PUNISHMENT, PLAYER_NUMBER, PRIVACY_RULE, TOTAL_TIME_LIMIT, TOTAL_TIME_PUNISHMENT)
    VALUES('pic', 'low', 50, 'FakeMoney', 'all', 2000, 'loose', 'two', 'everybody', 4000, 'loose');

    INSERT INTO GAME_SPECIFICATION (GAME_NAME, SPECIFICATION_NAME, PRICE, CURRENCY, GIVE_UP, MOVE_TIME_LIMIT, MOVE_TIME_PUNISHMENT, PLAYER_NUMBER, PRIVACY_RULE, TOTAL_TIME_LIMIT, TOTAL_TIME_PUNISHMENT)
    VALUES('pic', 'medium', 100, 'FakeMoney', 'all', 2000, 'loose', 'two', 'everybody', 4000, 'loose');

    INSERT INTO GAME_SPECIFICATION (GAME_NAME, SPECIFICATION_NAME, PRICE, CURRENCY, GIVE_UP, MOVE_TIME_LIMIT, MOVE_TIME_PUNISHMENT, PLAYER_NUMBER, PRIVACY_RULE, TOTAL_TIME_LIMIT, TOTAL_TIME_PUNISHMENT)
    VALUES('pic', 'high', 150, 'FakeMoney', 'all', 2000, 'loose', 'two', 'everybody', 4000, 'loose');

    INSERT INTO PLAYER_ACCOUNT (PLAYER_ID)
    VALUES(0);

    INSERT INTO PLAYER_ACCOUNT_AMOUNT (PLAYER_ID, CURRENCY, AMOUNT)
    VALUES(0, 0, 0);
