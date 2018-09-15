create table user (
  id integer not null auto_increment,
  name varchar(255) not null,
  username varchar(30) not null,
  last_sign_in_at datetime,
  role varchar(10) not null,
  created_at datetime,
  updated_at datetime,
  primary key (id)
)
engine=InnoDB;

insert into user (name, username, role, created_at, updated_at) values ('内立 良介', 'ruchitate', 'STAFF', '2018-10-01 00:00:00', '2018-10-01 00:00:00');
insert into user (name, username, role, created_at, updated_at) values ('山崎 賢人', 'kyamazaki', 'ADMIN', '2018-10-01 00:00:00', '2018-10-01 00:00:00');
