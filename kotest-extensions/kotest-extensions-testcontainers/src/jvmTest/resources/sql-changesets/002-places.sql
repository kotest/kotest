create table places (
   id serial primary key,
   city varchar(25) not null,
   state varchar(2) not null
);

INSERT INTO places (city, state)
VALUES
('Chicago', 'IL'),
('Reno', 'NV');
