

create database gid_db
go

use gid_db;

create table agent
(
	id int primary key identity,
	username char(16) unique not null
)

create table item
(
	id int primary key identity,
	title char(32),
	date_created datetime2,
	owner_id int references agent(id)
)

create table milestone
(
	id int primary key identity,
	descrip char(32),
	is_done bit,
	date_created datetime2,
	item_id int references item(id)
)

create table item_collaborator
(
	item_id int references item(id),
	collaborator_id int references agent(id)
)

create table collaborator
(
	agent_id int references agent(id),
	collaborator_id int references agent(id)
)
