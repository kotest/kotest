create table hashtags
(
   id  bigint auto_increment primary key,
   tag varchar(100) not null
);


insert into hashtags (tag)
values ('startrek');
