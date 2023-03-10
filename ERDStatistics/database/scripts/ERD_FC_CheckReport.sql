DELIMETER //
CREATE DEFINER=`root`@`localhost` FUNCTION `checkReport`(pID int) RETURNS boolean
    DETERMINISTIC
BEGIN
   DECLARE iValue int;
   SELECT count(*) into iValue from erd.report where ID = pID;
   IF (iValue > 0) then 
      return true;
   ELSE 
      return false;
   END IF;
END //
