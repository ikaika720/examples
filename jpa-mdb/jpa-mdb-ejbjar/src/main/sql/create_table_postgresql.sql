CREATE TABLE account (
    id bigint primary key,
    balance numeric(12,2) not null,
    lastUpdated timestamp not null
);

ALTER TABLE account OWNER TO user1;
