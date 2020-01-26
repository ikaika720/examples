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

CREATE INDEX transaction_account_index ON transaction(account);

ALTER INDEX transaction_account_index OWNER TO user01;

CREATE SEQUENCE transaction_id_seq START WITH 100;

ALTER SEQUENCE transaction_id_seq OWNER TO user01;
