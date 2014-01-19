CREATE DATABASE mydb;

CREATE USER user01;
ALTER USER user01 WITH ENCRYPTED PASSWORD 'password';

--DROP TABLE transaction;
--DROP TABLE account;
--DROP SEQUENCE transaction_id_seq;
CREATE TABLE account (
    id bigint primary key,
    balance numeric(12,2) not null,
    lastUpdated timestamp not null
);

ALTER TABLE account OWNER TO user01;

CREATE TABLE transaction (
    id bigint primary key,
    account bigint not null,
    date date not null,
    amount numeric(12,2) not null,
    running_balance numeric(12,2) not null,
    FOREIGN KEY(account) REFERENCES account(id)
);

ALTER TABLE transaction OWNER TO user01;

CREATE SEQUENCE transaction_id_seq START WITH 100;

ALTER SEQUENCE transaction_id_seq OWNER TO user01;

