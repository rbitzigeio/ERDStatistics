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
END