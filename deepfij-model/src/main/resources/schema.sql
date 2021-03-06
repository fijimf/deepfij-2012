alter table alias drop foreign key FK5899650C8E8797B;
alter table alias drop foreign key FK5899650A8A106F;
alter table conference drop foreign key FK2B5F451C3DFBFE40;
alter table game drop foreign key FK304BF23DFBFE40;
alter table game drop foreign key FK304BF2A1AFF1E9;
alter table game drop foreign key FK304BF26A200B1A;
alter table result drop foreign key FKC84DC81DF18A9F60;
alter table role_permission drop foreign key FKBD40D538FFBA31C0;
alter table role_permission drop foreign key FKBD40D5384F4A20E0;
alter table statParameter drop foreign key FKA93AABB522D71320;
alter table team drop foreign key FK36425D3DFBFE40;
alter table team drop foreign key FK36425DE2F8D2A0;
alter table teamStat drop foreign key FK9C8B4651A76F8380;
alter table teamStat drop foreign key FK9C8B465122D71320;
alter table user_role drop foreign key FK143BF46A4F4A20E0;
alter table user_role drop foreign key FK143BF46AF474E4C0;
drop table if exists alias;
drop table if exists conference;
drop table if exists game;
drop table if exists metaStat;
drop table if exists permission;
drop table if exists quote;
drop table if exists result;
drop table if exists role;
drop table if exists role_permission;
drop table if exists schedule;
drop table if exists statParameter;
drop table if exists team;
drop table if exists teamStat;
drop table if exists user;
drop table if exists user_role;
create table alias (id bigint not null auto_increment, alias varchar(255) not null, updatedAt datetime, scheduleId bigint, teamId bigint, primary key (id), unique (scheduleId, alias));
create table conference (id bigint not null auto_increment, keyName varchar(255), name varchar(255), updatedAt datetime, schedule_id bigint not null, primary key (id), unique (schedule_id, keyName), unique (schedule_id, name));
create table game (id bigint not null auto_increment, date date not null, isConferenceTournament bit not null, isNcaaTournament bit not null, isNeutralSite bit not null, updatedAt datetime, awayTeamId bigint, homeTeamId bigint, schedule_id bigint, primary key (id));
create table metaStat (id bigint not null auto_increment, format varchar(255), higherIsBetter bit, modelKey varchar(255), modelName varchar(255), name varchar(255), keyName varchar(255), primary key (id), unique (modelKey, keyName), unique (modelName, name));
create table permission (id bigint not null auto_increment, permission varchar(255) not null unique, updatedAt datetime not null, primary key (id));
create table quote (id bigint not null auto_increment, quote varchar(255) not null, source varchar(255), updatedAt datetime not null, url varchar(255), primary key (id));
create table result (id bigint not null auto_increment, awayScore integer, homeScore integer, updatedAt datetime, game_id bigint, primary key (id));
create table role (id bigint not null auto_increment, name varchar(255) not null unique, updatedAt datetime not null, primary key (id));
create table role_permission (permission_id bigint not null, role_id bigint not null, primary key (role_id, permission_id));
create table schedule (id bigint not null auto_increment, isPrimary bit, keyName varchar(255) unique, name varchar(255) unique, updatedAt datetime, primary key (id));
create table statParameter (id bigint not null auto_increment, date date, name varchar(255), value double precision, metaStat_id bigint not null, primary key (id), unique (metaStat_id, name, date));
create table team (id bigint not null auto_increment, keyName varchar(255) not null, logo varchar(255), longName varchar(255) not null, name varchar(255) not null, nickname varchar(255), officialUrl varchar(255), primaryColor varchar(255), secondaryColor varchar(255), updatedAt datetime, conference_id bigint, schedule_id bigint, primary key (id), unique (schedule_id, keyName), unique (schedule_id, longName), unique (schedule_id, name));
create table teamStat (id bigint not null auto_increment, date date, value double precision, metaStat_id bigint not null, team_id bigint not null, primary key (id), unique (metaStat_id, team_id, date));
create table user (id bigint not null auto_increment, email varchar(255) not null unique, password varchar(255) not null, updatedAt datetime not null, primary key (id));
create table user_role (role_id bigint not null, user_id bigint not null, primary key (user_id, role_id));
alter table alias add index FK5899650C8E8797B (teamId), add constraint FK5899650C8E8797B foreign key (teamId) references team (id);
alter table alias add index FK5899650A8A106F (scheduleId), add constraint FK5899650A8A106F foreign key (scheduleId) references schedule (id);
alter table conference add index FK2B5F451C3DFBFE40 (schedule_id), add constraint FK2B5F451C3DFBFE40 foreign key (schedule_id) references schedule (id);
alter table game add index FK304BF23DFBFE40 (schedule_id), add constraint FK304BF23DFBFE40 foreign key (schedule_id) references schedule (id);
alter table game add index FK304BF2A1AFF1E9 (awayTeamId), add constraint FK304BF2A1AFF1E9 foreign key (awayTeamId) references team (id);
alter table game add index FK304BF26A200B1A (homeTeamId), add constraint FK304BF26A200B1A foreign key (homeTeamId) references team (id);
alter table result add index FKC84DC81DF18A9F60 (game_id), add constraint FKC84DC81DF18A9F60 foreign key (game_id) references game (id);
alter table role_permission add index FKBD40D538FFBA31C0 (permission_id), add constraint FKBD40D538FFBA31C0 foreign key (permission_id) references permission (id);
alter table role_permission add index FKBD40D5384F4A20E0 (role_id), add constraint FKBD40D5384F4A20E0 foreign key (role_id) references role (id);
alter table statParameter add index FKA93AABB522D71320 (metaStat_id), add constraint FKA93AABB522D71320 foreign key (metaStat_id) references metaStat (id);
alter table team add index FK36425D3DFBFE40 (schedule_id), add constraint FK36425D3DFBFE40 foreign key (schedule_id) references schedule (id);
alter table team add index FK36425DE2F8D2A0 (conference_id), add constraint FK36425DE2F8D2A0 foreign key (conference_id) references conference (id);
alter table teamStat add index FK9C8B4651A76F8380 (team_id), add constraint FK9C8B4651A76F8380 foreign key (team_id) references team (id);
alter table teamStat add index FK9C8B465122D71320 (metaStat_id), add constraint FK9C8B465122D71320 foreign key (metaStat_id) references metaStat (id);
alter table user_role add index FK143BF46A4F4A20E0 (role_id), add constraint FK143BF46A4F4A20E0 foreign key (role_id) references role (id);
alter table user_role add index FK143BF46AF474E4C0 (user_id), add constraint FK143BF46AF474E4C0 foreign key (user_id) references user (id);

-------BELOW THIS LINE WILL NOT BE AUTO GENERATED-------;

insert into user(email, password, updatedAt) values ('fijimf@gmail.com', '7556a0d1970e9e8b7f5fa40110a6f4f998a66806d079095acc10108b6d610724', '1900-01-01');

insert into role(name, updatedAt) values ('ADMIN','1900-01-01' );
insert into role(name, updatedAt) values ('TRUSTED_USER','1900-01-01' );
insert into role(name, updatedAt) values ('REGISTERED_USER','1900-01-01' );

insert into user_role select user.id, role.id from user, role where user.email='fijimf@gmail.com' and role.name='ADMIN';