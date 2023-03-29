use erd;
select * from itsystem;
select * from report;
select * from report where ITSystem=7;
select * from bandwidtth;
select count(*) from bandwidth where ITSystem=(select ID from ITSystem where name ="OPTRA");
select * from bandwidth where ITSystem=(select ID from ITSystem where name ="AKOM") order by UnixTime desc;
select * from bandwidth where ITSystem=1 and UnixTime=1679786040;
select * from itsystem;
select count(*) from itsystem;
select count(*) from report where ITSystem=7;
select count(*) from bandwidth;
select * from bandwidth where ITSystem = (select ID from ITSystem where name ="AKOM") and UnixTime=1679954700;
select * from bandwidth where ITSystem = (select ID from ITSystem where name ="AKOM") order by FormattedTime;
select distinct(Date(FormattedTime)) from bandwidth where ITSystem = (select ID from ITSystem where name ='AKOM') order by Date(FormattedTime);
#-----------------------------------------------
# DELETE
delete from bandwidth where itsystem=7;
delete from report where ITSystem=7;
delete from itsystem where ID=15;
delete from bandwidth where ITSystem=2 and UnixTime=1679954700 and ReportID=279978;
#-----------------------------------------------
# DROP
drop procedure ERD.insertBandwidth;
drop procedure ERD.insertReport;
drop function ERD.checkReport;
drop function ERD.getIDofITSystem;