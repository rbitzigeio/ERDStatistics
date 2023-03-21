Delimiter //
#----------------------------------------- 
#
#
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertBandwidth`(IN uTime int, IN fTime timestamp, IN bitsIn double, IN bitsOut double, IN reportID int, IN itSystem int)
BEGIN 
   INSERT INTO ERD.Bandwidth (UnixTime, FormattedTime, BitsPerSecIn, BitsPerSecOut, ReportID, ITSystem)
   VALUES (uTime, fTime, bitsIn, bitsOut, reportID, itSystem);
END //

#----------------------------------------- 
#
#
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertReport`(IN pTitle varchar(255), IN pDescription varchar(255), IN pID int, IN pDate date, IN pSection varchar(80), IN pLineChart varchar(255), IN pFileName varchar(255), IN pITSystem int )
BEGIN 
   INSERT INTO ERD.Report (Title, Description, ID, CreationDate, Section, LineChart, FileName, ITSystem)
   VALUES (pTitle, pDescription, pID, pDate, pSection, pLineChart, pFileName, pITSystem);
END //

Delimiter ;

CREATE DEFINER=`root`@`localhost` PROCEDURE `printAllITSystems`()
BEGIN
    declare _sizeR int;
    declare _sizeB int;
    declare _id int;
    declare _name VARCHAR(32);
    declare cursor_ID int;
    declare cursor_Name VARCHAR(32);
    declare done INT default FALSE;
    declare cursor_i CURSOR FOR select id, name FROM ITSYSTEM;
    declare continue handler for not found set done = TRUE;
	drop table if exists ERD.tmpStatistik; 
    create table if not exists ERD.tmpStatistik ( 
	   ID INT,
       NAME Varchar(32),
       REPORT INT,
       BANDWIDTH INT  
    );
    
    OPEN cursor_i;
    read_loop: LOOP
        FETCH cursor_i INTO cursor_ID, cursor_Name;
        if done THEN
            leave read_loop;
		end if;
       
        select count(*) into _sizeR from report where ITSystem = cursor_ID;
		select count(*) into _sizeB from bandwidth where ITSystem = cursor_ID;
    
	    INSERT INTO ERD.tmpStatistik (ID, NAME, REPORT, BANDWIDTH)
	    VALUES (cursor_ID, cursor_Name, _sizeR, _sizeB); 
    END LOOP;
    CLOSE cursor_i;
    
    select * from erd.tmpStatistik;
END //

Delimiter ;

CREATE DEFINER=`root`@`localhost` PROCEDURE `printITSystem`(IN pName VARCHAR(32))
BEGIN
	declare _sizeR int;
    declare _sizeB int;
    declare _id int;
	select id into _id from itsystem where name = pName;
    select count(*) into _sizeR from report where ITSystem = _id;
    select count(*) into _sizeB from bandwidth where ITSystem = _id;
    select concat('Reports : ', _sizeR, ' Bandwidth : ', _sizeB, ' ID : ',  _id);
END//

Delimiter ;