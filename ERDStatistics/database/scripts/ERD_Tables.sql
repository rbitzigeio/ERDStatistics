# Script to create Database schema, user and tables to share 
# statistics of ExpressRouteDirect
# root user 
# K...! || O...5 || H..B..7
# 18.12.2022
# RBo
#------------------------------------------------------------ 
# Schema
create database if not exists ERD;
#------------------------------------------------------------
# User and access
create user if not exists 'erd'@'localhost' identified by 'erd';
grant all on ERD.* to 'erd'@'localhost';
#------------------------------------------------------------
# Tables
create table if not exists ERD.Bandwidth 
(
UnixTime INT NOT NULL,
FormattedTime TIMESTAMP NOT NULL,
BitsPerSecIn DOUBLE NOT NULL,
BitsPerSecOut DOUBLE NOT NULL,
ReportID INT,
ITSystem INT
);
ALTER TABLE ERD.Bandwidth ADD UNIQUE (UnixTime, ReportID, ITSystem);
#
#------------------------------------------------------------
#
create table if not exists ERD.Report
(
Title VARCHAR(255) NOT NULL,
Description VARCHAR(255) NOT NULL,
ID INT NOT NULL,
CreationDate Date NOT NULL,
Section VARCHAR(80),
LineChart VARCHAR(255),
FileName VARCHAR(128),
ITSystem INT
);
ALTER TABLE ERD.Report ADD UNIQUE (ID, CreationDate, ITSystem); 
#
#------------------------------------------------------------
#
CREATE TABLE if not exists ERD.ITSystem
(
NAME VARCHAR(32) NOT NULL,
ID INT NOT NULL Primary Key,
ICTO VARCHAR(32)
);
#
#------------------------------------------------------------
#
CREATE TABLE if not exists `maxbandwidth` (
  `id` int NOT NULL AUTO_INCREMENT Primary Key,
  `itsystem` int NOT NULL,
  `reportid` int NOT NULL,
  `date` date NOT NULL,
  `maxbandwidthin` double NOT NULL,
  `maxbandwidthout` double NOT NULL,
  `timein` datetime NOT NULL,
  `timeout` datetime NOT NULL
);
