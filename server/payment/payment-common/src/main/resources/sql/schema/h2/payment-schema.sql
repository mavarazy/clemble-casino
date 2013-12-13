
    drop table PAYMENT_TRANSACTION if exists;

    drop table PAYMENT_TRANSACTION_OPERATION if exists;

    drop table PLAYER_ACCOUNT if exists;

    drop table PLAYER_ACCOUNT_AMOUNT if exists;

    create table PAYMENT_TRANSACTION (
        TRANSACTION_SOURCE varchar(255) not null,
        TRANSACTION_ID varchar(255) not null,
        TRANSACTION_PROCESSING_DATE timestamp,
        TRANSACTION_DATE timestamp,
        primary key (TRANSACTION_SOURCE, TRANSACTION_ID)
    );

    create table PAYMENT_TRANSACTION_OPERATION (
        TRANSACTION_ID varchar(255) not null,
        MONEY_SOURCE varchar(255) not null,
        CURRENCY integer,
        AMOUNT bigint,
        OPERATION varchar(255),
        PLAYER_ID varchar(255)
    );

    create table PLAYER_ACCOUNT (
        PLAYER_ID varchar(255) not null,
        primary key (PLAYER_ID)
    );

    create table PLAYER_ACCOUNT_AMOUNT (
        PLAYER_ID varchar(255) not null,
        CURRENCY integer,
        AMOUNT bigint
    );

    alter table PAYMENT_TRANSACTION_OPERATION 
        add constraint FK_jys2g64i2unkwh385gs3x7k1 
        foreign key (TRANSACTION_ID, MONEY_SOURCE) 
        references PAYMENT_TRANSACTION;

    alter table PLAYER_ACCOUNT_AMOUNT 
        add constraint FK_fbj51f8hoakoj2ark4h34igxj 
        foreign key (PLAYER_ID) 
        references PLAYER_ACCOUNT;
