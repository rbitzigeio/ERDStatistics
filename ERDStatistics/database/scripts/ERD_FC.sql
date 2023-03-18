Delimiter //

CREATE DEFINER=`root`@`localhost` FUNCTION `checkReport`(pID int, pDate date, pItSystem int ) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
   DECLARE iValue INT;
   SELECT count(*) into iValue from erd.report where ID = pID and CreationDate = pDate and ITSystem = pItSystem;
   IF (iValue > 0) then
      return true;
   ELSE
      return false;
   END IF;
END //

CREATE DEFINER=`root`@`localhost` FUNCTION `checkReportById`(pID int) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
   DECLARE iValue INT;
   SELECT count(*) into iValue from erd.report where ID = pID;
   IF (iValue > 0) then 
      return true;
   ELSE 
      return false;
   END IF;
END //

CREATE DEFINER=`root`@`localhost` FUNCTION `getIDofITSystem`(pName VARCHAR(32)) RETURNS int
    DETERMINISTIC
BEGIN
    DECLARE pID INT;
    DECLARE pMaxID INT;
    SELECT ID INTO pID from ITSystem where NAME = pName;
    IF (pID IS NULL) then
       SELECT MAX(ID) INTO pID FROM ITSystem;
       IF (pID IS NULL) then
          SET pID = 1;
       ELSE
          SET pID = pID + 1;
	   END IF;
	   INSERT INTO ERD.ITSystem(NAME, ID) VALUES(pName,pID); 
    END IF;
    RETURN pID;
END //

Delimiter ;
