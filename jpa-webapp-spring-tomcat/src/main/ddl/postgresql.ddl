CREATE DATABASE mydb;

CREATE USER user01;
ALTER USER user01 WITH ENCRYPTED PASSWORD 'password';

CREATE TABLE member (
    id bigint primary key,
    name text,
    email text,
    dateOfBirth date
);

ALTER TABLE member OWNER TO user01;

CREATE TABLE member_member (
    member_id bigint,
    friends_id bigint,
    PRIMARY KEY(member_id, friends_id)
);

ALTER TABLE member_member OWNER TO user01;
