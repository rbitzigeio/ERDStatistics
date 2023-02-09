# Script to create Database schema, user and tables to share 
# statistics of ExpressRouteDirect
# root user 
# K...! || O...5
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
UnixTime INT NOT NULL unique,
FormattedTime TIMESTAMP NOT NULL,
BitsPerSecIn DOUBLE NOT NULL,
BitsPerSecOut DOUBLE NOT NULL,
ReportID INT
);
create table if not exists ERD.Report
(
Title VARCHAR(255) NOT NULL,
Description VARCHAR(255) NOT NULL,
ID INT NOT NULL UNIQUE,
Section VARCHAR(80),
LineChart VARCHAR(128),
FileName VARCHAR(128),
ITSystem VARCHAR(32)
);
