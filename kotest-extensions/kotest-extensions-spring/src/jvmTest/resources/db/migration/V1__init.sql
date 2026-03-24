CREATE TABLE kotest_user
(
   pk_id    BIGINT PRIMARY KEY,
   username TEXT UNIQUE NOT NULL
);

CREATE SEQUENCE kotest_user_pk_sequence START 1 INCREMENT 1;
