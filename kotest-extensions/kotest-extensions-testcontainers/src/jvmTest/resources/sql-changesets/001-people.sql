create table people (
   id serial primary key,
   name varchar(25) not null,
   is_cool boolean
);
-- THIS COMMENT SHOULD BE IGNORED
INSERT INTO people (name, is_cool)
VALUES
('Scott', true),
('Sam', false);
