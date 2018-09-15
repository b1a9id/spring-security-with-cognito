create table if not exists user(
    id int(11) unsigned auto_increment primary key,
    name varchar(255) not null,
    username varchar(255) not null,
    last_sign_in_at datetime null,
    role varchar(10) not null,
    created_at datetime default CURRENT_TIMESTAMP not null,
    updated_at datetime null on update CURRENT_TIMESTAMP
);

INSERT INTO user (id, name, username, role, created_at, updated_at) VALUES (1, '内立 良介', 'ruchitate', 'STAFF', '2018-07-24 07:51:34', '2018-07-24 09:28:42');
INSERT INTO user (id, name, username, role, created_at) VALUES (2, '山崎 賢人', 'kyamazaki', 'ADMIN', '2018-07-27 09:14:40');
