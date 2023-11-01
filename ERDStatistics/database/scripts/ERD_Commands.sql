use erd;
select * from itsystem;
select * from report;
select * from report where ITSystem=1 order by creationDate desc;
select Min(CreationDate) from report where ITSystem=1;
select Max(CreationDate) from report where ITSystem=1;
select ID from report where ITSystem = 1 and CreationDate = (select Min(CreationDate) from report where ITSystem=1);
select ID from report where ITSystem = 1 and CreationDate = (select Max(CreationDate) from report where ITSystem=1);
select * from bandwidth where ReportID = 349464;
select * from report where ITSystem=2 and ID=308695;
select * from bandwidtth;
select count(*) from bandwidth where ITSystem=(select ID from ITSystem where name ="OPTRA");
select count(*) from bandwidth group by ITSystem order by ITSystem;
select count(*) from report group by ITSystem order by ITSystem;
select count(*) from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") and Date(FormattedTime) = "2023-09-25";
select Date(FormattedTime), Max(BitsPerSecIn), Max(BitsPerSecOut) from bandwidth where ITSystem=1 and Date(FormattedTime) and Date(FormattedTime) between "2023-09-25" and "2023-10-01" group by Date(FormattedTime) order by Date(FormattedTime);
select Date(FormattedTime), Max(BitsPerSecIn), Max(BitsPerSecOut) from bandwidth where ITSystem=(select ID from ITSystem where name ='Infrastruktur') and Date(FormattedTime) between '2023-10-03' and '2023-10-03' group by Date(FormattedTime) order by Date(FormattedTime);
select count(*) from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") and ReportID=308695;
select * from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") and UnixTime=1680560100 order by UnixTime desc;
select * from bandwidth where ITSystem=1 and UnixTime=1679786040;
select sum(bitspersecin) from bandwidth where Unixtime=167986040;
SELECT FormattedTime, Sum(BitsPerSecIn)/8/1000000 FROM ERD.bandwidth where FormattedTime between "2023-10-03 00:00:00.0" and "2023-10-03 23:59:59.0" group by FormattedTime;
select * from itsystem;
select count(*) from itsystem;
select count(*) from report where ITSystem=7;
select * from bandwidth where Date(FormattedTime) ="2023-04-04" and ITSystem=2;
select count(*) from bandwidth where ITSystem=2 and ReportID=281700;
select count(*) from bandwidth where Date(FormattedTime) ="2023-04-03" and ITSystem=1;
select * from bandwidth where FormattedTime="2023-04-04 00:16:00" and ITSystem=2;
select * from report where CreationDate ="2023-06-01" order by ID;
select * from bandwidth where ITSystem = (select ID from ITSystem where name ="AKOM") and UnixTime=1680560100;
select * from bandwidth where ITSystem = (select ID from ITSystem where name ="AKOM") order by FormattedTime;
select distinct(Date(FormattedTime)) from bandwidth where ITSystem = (select ID from ITSystem where name ='AKOM') order by Date(FormattedTime);
select * from bandwidth where ITSystem = (select ID from ITSystem where name ='AKOM') and Date(FormattedTime) between '2023-03-01' and '2023-03-02';
#-----------------------------------------------
# INDEXES 
select Max(BitsPerSecIn) from Bandwidth where ITSystem = 1;
show indexes from bandwidth;
create index idx_FormattedTime on bandwidth(FormattedTime);
create index idx_ReportID on bandwidth(ReportID);
select * from bandwidth where ITSystem = 1 and UnixTime > 169628400 and UnixTime < 1696370340;
select * from bandwidth where ITSystem = (select ID from ITSystem where name ='Infrastruktur') and Date(FormattedTime) = '2023-10-03' order by FormattedTime;
select * from bandwidth where ITSystem = 1 and ReportID=327725;
select * from bandwidth where ITSystem = 1 and ReportID=328670;
select * from bandwidth where ITSystem = (select ID from ITSystem where name ='Infrastruktur') and ReportID = 327725;
select ID from Report where ITSystem = (select ID from ITSystem where name ='Infrastruktur') and Date(CreationDate) = '2023-10-03';
#-----------------------------------------------
# DELETE
delete from bandwidth where itsystem=7;
delete from bandwidth where reportID=308695;
delete from bandwidth where Date(FormattedTime) ="2023-04-05";
delete from report where ITSystem=7;
select * from report where ID=308695;
select * from bandwidth where ReportID=308695;
delete from report where ID=308695;
delete from report where CreationDate ="2023-04-04";
delete from itsystem where ID=15;
delete from bandwidth where ITSystem=2 and UnixTime=1679954700 and ReportID=279978;
#-----------------------------------------------
# DROP
drop procedure ERD.insertBandwidth;
drop procedure ERD.insertReport;
drop function ERD.checkReport;
drop function ERD.getIDofITSystem;