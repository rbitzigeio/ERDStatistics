# Script to create Database schema, user and tables to share 
# statistics of ExpressRouteDirect
# 18.12.2022
# RBo
#------------------------------------------------------------ 
# Schema
create database if not exists ERD;
#------------------------------------------------------------
# User and access
create user 'erd'@'localhost' identified by 'erd';
grant all on ERD.* to 'erd'@'localhost';
#------------------------------------------------------------
# Tables
create table if not exists ERD.Bandwidth 
(
UnixTime int NOT NULL unique,
FormattedTime date NOT NULL,
BitsPerSecIn double NOT NULL,
BitsPerSecOut double NOT NULL
);

