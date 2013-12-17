CREATE DATABASE mydb;

CREATE USER user01;
ALTER USER user01 WITH ENCRYPTED PASSWORD 'password';

CREATE TABLE table01 (
    id integer primary key,
    data1 text,
    data2 text,
    created timestamp,
    updated timestamp
);

ALTER TABLE table01 OWNER TO user01;
