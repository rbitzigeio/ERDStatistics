DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertBandwidth`(IN uTime int, IN fTime timestamp, IN bitsIn double, IN bitsOut double, IN reportID int)
BEGIN 
   INSERT INTO ERD.Bandwidth (UnixTime, FormattedTime, BitsPerSecIn, BitsPerSecOut, ReportID)
   VALUES (uTime, fTime, bitsIn, bitsOut, reportID);
END //
#
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertReport`(IN pTitle varchar(255), IN pDescription varchar(255), IN pID int, IN pDate date, IN pSection varchar(80), IN pLineChart varchar(255), IN pFileName varchar(255), IN pITSystem varchar(32) )
BEGIN 
   INSERT INTO ERD.Report (Title, Description, ID, CreationDate, Section, LineChart, FileName, ITSystem)
   VALUES (pTitle, pDescription, pID, pDate, pSection, pLineChart, pFileName, pITSystem);
END //
