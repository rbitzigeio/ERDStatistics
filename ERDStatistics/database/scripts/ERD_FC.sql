#
CREATE DEFINER=`root`@`localhost` FUNCTION `checkReport`(pID int) RETURNS boolean
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
#
CREATE DEFINER=`root`@`localhost` FUNCTION `getIDofITSystem`(pName VARCHAR(32)) RETURNS boolean
DETERMINISTIC
BEGIN
    DECLARE pID INT;
    DECLARE pMaxID INT;
    SELECT ID INTO pID from ITSystem where NAME = pName;
    IF (pID IS NULL) then
       SELECT MAX(ID) INTO pID FROM ITSystem;
       SET pID = pID + 1;
       INSERT INTO ERD.ITSystem(NAME, ID) VALUES(NAME,pID); 
    END IF;
    RETURN pID;
END //
