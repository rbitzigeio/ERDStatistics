use erd;
select * from ERD.Report;
delete from ERD.Report;
select * from ERD.Bandwidth;
delete from ERD.Bandwidth;
drop procedure ERD.insertBandwidth;
drop procedure ERD.insertReport;
drop function ERD.checkReport;
drop function ERD.getIDofITSystem;
