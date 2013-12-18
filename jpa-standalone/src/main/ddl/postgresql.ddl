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
