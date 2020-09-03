create schema if not exists bank;

create table if not exists bank.user
(
    id               int auto_increment primary key,
    name             varchar2(20) not null,
    login            varchar2(1) not null,
    creation_time    timestamp default systimestamp
);

create table if not exists bank.txn
(
    id               int auto_increment primary key,
    user_id          number(15) not null,
    amount           number(18, 2) not null,
    type             varchar2(1) not null,
    txn_id           number(15) not null,
    ref_id           number(15) not null,
    status           varchar2(1) not null,
    creation_time    timestamp default systimestamp
);