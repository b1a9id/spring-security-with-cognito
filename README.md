drop database cognito;
create database cognito character set utf8;
grant all privileges on cognito.* to 'cognito'@'localhost' identified by 'cognito' with grant option;
grant all privileges on cognito.* to 'cognito'@'%' identified by 'cognito' with grant option;
