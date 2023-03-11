CREATE DEFINER=`root`@`localhost` PROCEDURE `insertReport`(IN pTitle varchar(255), IN pDescription varchar(255), IN pID int, IN pDate date, IN pSection varchar(80), IN pLineChart varchar(255), IN pFileName varchar(255), IN pITSystem int )
BEGIN 
   INSERT INTO ERD.Report (Title, Description, ID, CreationDate, Section, LineChart, FileName, ITSystem)
   VALUES (pTitle, pDescription, pID, pDate, pSection, pLineChart, pFileName, pITSystem);
END