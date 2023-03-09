DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertReport`(IN pTitle varchar(255), IN pDescription varchar(255), IN pID int, IN pSection varchar(80), IN pLineChart varchar(255), IN pFileName varchar(255), IN pITSystem varchar(32) )
BEGIN 
   INSERT INTO ERD.Report (Title, Description, ID, Section, LineChart, FileName, ITSystem)
   VALUES (pTitle, pDescription, pID, pSection, pLineChart, pFileName, pITSystem);
END //
