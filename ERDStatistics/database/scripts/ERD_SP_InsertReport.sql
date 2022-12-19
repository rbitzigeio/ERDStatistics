DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertReport`(IN pTitle varchar(255), IN pDdescription varchar(128), IN pID int, IN pSection varchar(80), IN pLineChar varchar(128))
BEGIN 
   INSERT INTO ERD.Report (Title, Description, ID, Section, LineChar)
   VALUES (pTitle, pDescription, pID, pSection, pLineChar);
END //
