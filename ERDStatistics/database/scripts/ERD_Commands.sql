use erd;
select * from itsystem;
select * from report;
select * from bandwitdth;
select * from itsystem;
select count(*) from itsystem;
select count(*) from report where ITSystem=9;
select count(*) from bandwidth where itsystem=9;
#-----------------------------------------------
# DELETE
delete from bandwidth where itsystem=9;
delete from report where ITSystem=9;
delete from itsystem where ID=9;
#-----------------------------------------------
# DROP
drop procedure ERD.insertBandwidth;
drop procedure ERD.insertReport;
drop function ERD.checkReport;
drop function ERD.getIDofITSystem;