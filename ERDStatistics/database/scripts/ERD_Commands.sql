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
select count(*) from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") and Date(FormattedTime) = "2023-09-26";
select count(*) from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") and ReportID=308695;
select * from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") and UnixTime=1680560100 order by UnixTime desc;
select * from bandwidth where ITSystem=1 and UnixTime=1679786040;
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