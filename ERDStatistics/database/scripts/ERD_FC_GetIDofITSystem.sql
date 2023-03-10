DELIMETER //
CREATE DEFINER=`root`@`localhost` FUNCTION `checkReport`(pID int) RETURNS boolean
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
